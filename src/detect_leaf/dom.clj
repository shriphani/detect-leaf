(ns detect-leaf.dom
  "Dom-specific codebase for classifier features"
  (:use [clj-xpath.core :only [$x $x:text+ $x:node+]])
  (:import [org.tartarus.snowball SnowballStemmer]
           [org.tartarus.snowball.ext englishStemmer]
           (org.htmlcleaner HtmlCleaner DomSerializer CleanerProperties)))

(defn html->xml-doc
  "Converts a html string to an XML object"
  [page-src]
  (let [cleaner (new HtmlCleaner)
        props (.getProperties cleaner)
        _ (.setPruneTags props "script, style")
        _ (.setOmitComments props true)
        cleaned-text (.clean cleaner page-src)
        cleaner-props  (new CleanerProperties)

        dom-serializer (new DomSerializer cleaner-props)

        node (.createDOM dom-serializer cleaned-text)]
    node))

(defn anchor-nodes
  [a-node]
  ($x:node+ ".//a" a-node))

(defn node-to-root-path
  ([a-node]
     (node-to-root-path a-node []))
  ([a-node cur-path]
   (if (= (.getNodeName a-node) "html")
     (str
      "/"
      (clojure.string/join
       "/"
       (map
        (fn [[n c]]
          (if c
            (format "%s[contains(@class,'%s')]" n c)
            n))
        (cons ["html" nil] cur-path))))
     (recur (.getParentNode a-node)
            (cons [(.getNodeName a-node)
                   (try (-> a-node
                            (.getAttributes)
                            (.getNamedItem "class")
                            (.getValue))
                        (catch Exception e nil))] cur-path)))))

(defn grouped-anchors
  [page-src]
  (let [the-node (html->xml-doc page-src)
        anchors  (anchor-nodes the-node)
        paths    (map
                  (fn [a]
                    [(node-to-root-path a) a])
                  anchors)]
    (into
     {}
     (map
      (fn [[x ns]]
        [x (map second ns)])
      (group-by first paths)))))

(defn node-attr
  [a-node]
  [(.getNodeName a-node)
   (try
     (-> a-node
         (.getAttributes)
         (.getNamedItem "class")
         (.getValue))
     (catch Exception e nil))])

(defn get-nodes-to-root
  ([a-node]
     (get-nodes-to-root a-node []))
  ([a-node path]
     (if (= (.getNodeName a-node) "html")
       (cons a-node path)
       (recur (.getParentNode a-node) (cons a-node path)))))

(defn histograms
  [[path nodes]]
  (distinct
   (for [target-node nodes]
     (let [nodes-to-root (get-nodes-to-root target-node)
           nodes-pairs (map vector nodes-to-root (rest nodes-to-root))]
       (reduce
        (fn [acc [p c]]
          (let [c-attr (node-attr c)
                p-children (.getChildNodes p)
                p-children-n (.getLength p-children)
                pcs (map (fn [i] (.item p-children i)) (range p-children-n))
                n-pcs (count
                       (filter (fn [node]
                                 (= (node-attr node)
                                    c-attr)) pcs))]
            (cons n-pcs acc)))
        []
        nodes-pairs)))))

(defn page-histogram-candidates
  [page-grouped-anchors]
  (filter
   (fn [x]
     (let [histogram (histograms x)]
       (and (= 1 (count histogram))
            (>= (count (filter #(> % 1) (first histogram)))
                2))))
   page-grouped-anchors))

(defn page-histogram-links
  [page-src]
  (let [as (grouped-anchors page-src)
        histogram-candidates 
        (page-histogram-candidates as)]
    (distinct
     (reduce
      (fn [acc [path ns]]
        (concat
         (distinct
          (map
           (fn [n]
             (try
               (-> n
                   (.getAttributes)
                   (.getNamedItem "href")
                   (.getValue))
               (catch Exception e nil)))
           ns))
         acc))
      []
      histogram-candidates))))

(defn page-histogram-text
  [page-src]
  (let [as (grouped-anchors page-src)
        histogram-candidates 
        (page-histogram-candidates as)]
    (distinct
     (reduce
      (fn [acc [path ns]]
        (concat
         (distinct
          (map
           (fn [n]
             (try
               (.getTextContent n)
               (catch Exception e nil)))
           ns))
         acc))
      []
      histogram-candidates))))

(defn num-page-histogram-links
  [page-src]
  (-> page-src
      page-histogram-links
      count))


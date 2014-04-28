(ns detect-leaf.features
  "Features for this classifier"
  (:require [clojure.java.io :as io]
            [clojure.set :as clj-set]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as html])
  (:use [clj-xpath.core :only [$x $x:text+]])
  (:import [org.tartarus.snowball SnowballStemmer]
           [org.tartarus.snowball.ext englishStemmer]
           (org.htmlcleaner HtmlCleaner DomSerializer CleanerProperties)))

(def stoplist (-> "stoplist.txt"
                  io/resource
                  slurp
                  string/split-lines
                  set))

(defn text->cleaned-tokens
  [a-document]
  (let [tokens (string/split a-document #"\s+|\p{Punct}|\d+")]
    (map string/lower-case tokens)))

(defn stem
  ([tok]
     (stem (new englishStemmer)
           tok))
  ([stemmer t]
     (do (.setCurrent stemmer t)
         (.stem stemmer)
         (.getCurrent stemmer))))

(def stemmed-stoplist
  (let [stemmer (new englishStemmer)]
    (set
     (map
      (fn [t] (stem stemmer t))
      stoplist))))

(defn text->cleaned-stemmed-tokens
  [a-document]
  (let [tokens (string/split a-document #"\s+|\p{Punct}|\d+")
        stemmer (new englishStemmer)]
    (map
     (fn [t]
       (let [lower-tok (string/lower-case t)]
         (stem stemmer lower-tok)))
     tokens)))

(defn language-model
  [a-document]
  (let [tokens (text->cleaned-tokens a-document)]
    (into
     {}
     (map
      (fn [[w n]]
        [w (/ n (count tokens))])
      (frequencies tokens)))))

(defn stemmed-language-model
  [a-document]
  (let [tokens (text->cleaned-stemmed-tokens a-document)]
    (into
     {}
     (map
      (fn [[w n]]
        [w (/ n (count tokens))])
      (frequencies tokens)))))

(defn k-l-divergence
  [model1 model2]
  (let [tokens (keys model1)]
    (reduce
     (fn [acc t]
       (let [p-x (model1 t)
             q-x (model2 t)]
         (+
          acc
          (if (and p-x q-x)
           (* (Math/log (/ p-x q-x))
              p-x)
           0))))
     0
     tokens)))

(defn avg-k-l-divergence
  [model1 model2]
  (/ (+ (k-l-divergence model1 model2)
        (k-l-divergence model2 model1))
     2))

(defn jaccard-sim
  [model1 model2]
  (let [model1-keys (set (map first model1))
        model2-keys (set (map first model2))

        intersection (clojure.set/intersection model1-keys
                                               model2-keys)

        union (clojure.set/union model1-keys
                                 model2-keys)]
    (/ (count intersection)
       (count union))))

(defn anchor-text-tokens
  [anchor-text]
  (count
   (string/split anchor-text #"\s+")))

(defn count-stopwords
  [text]
  (let [cleaned-stemmed-tokens (text->cleaned-stemmed-tokens text)]
    (count
     (filter
      (fn [t]
        (some #{t} stemmed-stoplist))
      cleaned-stemmed-tokens))))

(defn anchor-text-page
  [text]
  (string/join
   " "
   (map
    html/text
    (-> text
        java.io.StringReader.
        html/html-resource
        (html/select [:a])))))

(defn single-token-anchor-texts
  [text]
  (count
   (filter
    (fn [x]
      (= 1 (count (string/split x #"\s+"))))
    (map
     html/text
     (-> text
         java.io.StringReader.
         html/html-resource
         (html/select [:a]))))))

(defn anchor-texts
  [text]
  (map
   html/text
   (-> text
       java.io.StringReader.
       html/html-resource
       (html/select [:a]))))

(defn texts
  [text]
  (let [cleaner (new HtmlCleaner)
        props (.getProperties cleaner)
        _ (.setPruneTags props "script, style")
        _ (.setOmitComments props true)
        cleaned-text (.clean cleaner text)

        cleaner-props  (new CleanerProperties)

        dom-serializer (new DomSerializer cleaner-props)

        node (.createDOM dom-serializer cleaned-text)]
    (filter
     (fn [x]
       (not= "" x))
     (map clojure.string/trim ($x:text+ ".//text()" node)))))

(defn compute-features
  [{anchor-text :anchor-text
    url :url
    body :body
    label :label}]
  (let [anchor-text-language-model (try (language-model anchor-text)
                                        (catch NullPointerException e {}))

        body-text (-> body
                      java.io.StringReader.
                      html/html-resource
                      (html/select [:html])
                      first
                      html/text)

        body-language-model (language-model body-text)

        anchor-text (anchor-text-page body)

        single-token-anchors (single-token-anchor-texts body)

        anchors (anchor-texts body)

        positions (distinct (sort (map (fn [t] (.indexOf body-text t)) anchors)))
        
        avg-anchor-l (double
                      (/ (apply + (map count (anchor-texts body)))
                         (count (anchor-texts body))))

        gaps (map
              (fn [[x y]] (- x y))
              (map vector (rest positions) positions))

        avg-gap (double
                 (/ (apply + gaps)
                    (count gaps)))

        var-gap (/
                 (reduce
                  (fn [acc g]
                    (+ (Math/pow (- g avg-gap) 2) acc))
                  0
                  gaps)
                 (count gaps))

        body-texts (texts body)
        
        text-positions (distinct (sort (map (fn [t] (.indexOf body-text t)) body-texts)))

        text-gaps (map
                   (fn [[x y]] (- x y))
                   (map vector (rest text-positions) text-positions))

        avg-txt-gap (double
                     (/ (apply + text-gaps)
                        (count text-gaps)))]
    [(if (nil? anchor-text)
       0.0
       (double
        (jaccard-sim
         anchor-text-language-model
         body-language-model)))
     (count anchor-text-language-model)
     avg-gap
     avg-txt-gap
     (count-stopwords anchor-text)
     (count-stopwords body-text)
     single-token-anchors
     (if label 1 0)]))

(defn compute-features-csv
  [x]
  (->> x compute-features (string/join ",")))

(defn compute-features-libsvm
  [x]
  (let [stuff (->> x compute-features)
        label (last stuff)
        pts   (drop-last stuff)]
    (str
     (if (= label 1) "+1" "-1")
     " "
     (string/join
      " "
      (map
       (fn [[x y]]
         (str x ":" y))
       (map vector
            (map
             inc
             (range
              (count pts)))
            pts))))))

(defn compute-features-map
  [x]
  (let [fs (drop-last (compute-features x))]
    (into
     {}
     (map vector
          (map
           inc
           (range
            (count fs)))
          fs))))

(ns detect-leaf.core
  (:require [clojure.tools.cli :as cli]
            [detect-leaf.utils :as utils]
            [org.bovinegenius [exploding-fish :as uri]])
  (:use [clj-xpath.core :only [$x $x:node $x:node+ $x:text+]]
        [clojure.pprint :only [pprint]]
        [structural-similarity.xpath-text :only [similar?]])
  (:import (org.htmlcleaner HtmlCleaner DomSerializer CleanerProperties)
           (org.w3c.dom Document)))

(defn random-sample
  [url-queue visited extract stop? corpus]
  (if (or (empty? url-queue)
          (stop? (count visited)))
    corpus
    (do (Thread/sleep 1000)
        (let [tok (rand-int 2)
              
              picked-url (if (zero? tok) (first url-queue) (last url-queue))
              body-queue-new (if (zero? tok)
                               (rest url-queue)
                               (drop-last url-queue))
              
              body (try (utils/download-with-cookie (:url picked-url))
                        (catch Exception e nil))
              updated-corpus (cons {:url (:url picked-url)
                                    :body body} corpus)
              extracted-links (and
                               body
                               (distinct
                                (extract body picked-url (clojure.set/union
                                                          (set
                                                           (map :url url-queue))
                                                          (set visited)))))]
          (utils/sayln :url (:url picked-url))
          (utils/sayln :visited (count visited))
          (utils/sayln :left (count url-queue))
          (utils/sayln :extracted (count extracted-links))
          
          (recur (concat body-queue-new
                         (map
                          (fn [x] {:url x})
                          extracted-links))
                 (conj visited (:url picked-url))
                 extract
                 stop?
                 updated-corpus)))))

(defn process-page
  [page-src]
  (let [cleaner (new HtmlCleaner)
        props   (.getProperties cleaner)
        _       (.setPruneTags props "script, style")
        _       (.setOmitComments props true)]
    (.clean cleaner page-src)))

(defn html->xml-doc
  "Take the html and produce an xml version"
  [page-src]
  (let [tag-node       (-> page-src
                           process-page)

        cleaner-props  (new CleanerProperties)

        dom-serializer (new DomSerializer cleaner-props)]
    
    (-> dom-serializer
        (.createDOM tag-node))))

(defn links-on-page
  [page-src]
  (let [processed-pg (html->xml-doc page-src)
        anchors      ($x ".//a" processed-pg)]
    (map #(-> % :attrs :href) anchors)))

(defn extract-in-domain-links
  [body url-ds blacklist]
  (let [ls (links-on-page body)]
    (filter
     (fn [x]
       (not (some #{x} blacklist)))
     (filter
      identity
      (filter
       #(= (-> % uri/host)
           (uri/host
            (:url url-ds)))
       (filter
        identity
        (map #(try (uri/resolve-uri (:url url-ds) %)
                   (catch Exception e nil)) ls)))))))

(defn fetch-random-corpus
  [start-url limit]
  (random-sample [{:url start-url}]
                 (set [])
                 extract-in-domain-links
                 #(<= limit %)
                 {}))

(defn belongs-to-cluster-single?
  [x members]
  (some (fn [m] (similar? (:body m) (:body x))) members))

(defn belongs-to-cluster-single?
  [x members]
  (every? (fn [m] (similar? (:body m) (:body x))) members))

(defn cluster-corpus-single
  [a-corpus]
  (reduce
   (fn [clusters x]
     (let [assignment (.indexOf
                       (map
                        (fn [c]
                          (belongs-to-cluster-single? x c))
                        clusters)
                       true)]
       (if (neg? assignment)
         (conj clusters [x])
         (assoc clusters assignment (conj (nth clusters assignment) x)))))
   []
   a-corpus))

(defn cluster-corpus-strong
  [a-corpus]
  (reduce
   (fn [clusters x]
     (let [assignment (.indexOf
                       (map
                        (fn [c]
                          (belongs-to-cluster-strong? x c))
                        clusters)
                       true)]
       (if (neg? assignment)
         (conj clusters [x])
         (assoc clusters assignment (conj (nth clusters assignment) x)))))
   []
   a-corpus))

(defn identify-leaf
  [corpus option]
  (cond (= :single option)
        (map :url (last
                   (sort-by count (cluster-corpus-single corpus))))

        (= :strong option)
        (map :url (last
                   (sort-by count (cluster-corpus-strong corpus))))))

(def options
  [[nil "--download-corpus U" "download a corpus"]
   [nil "--cluster-corpus C" "cluster the corpus"]
   [nil "--limit N" "cluster the corpus"
    :default 500
    :parse-fn #(Integer/parseInt %)]
   [nil "--single" "Use single linkage for clustering"]
   [nil "--strong" "Use strong linkage for clustering"]])

(defn -main
  [& args]
  (let [options (-> args
                    (cli/parse-opts options)
                    :options)]
    (cond (:download-corpus options)
          (let [wrtr (clojure.java.io/writer
                      (str
                       (uri/host
                        (:download-corpus options))
                       ".corpus"))
                stuff (fetch-random-corpus
                       (:download-corpus options)
                       (:limit options))]
            (pprint stuff wrtr))

          (:cluster-corpus options)
          (let [corpus (read
                        (java.io.PushbackReader.
                         (clojure.java.io/reader
                          (:cluster-corpus options))))
                wrtr (clojure.java.io/writer
                      (clojure.string/replace
                       (:cluster-corpus options)
                       #".corpus"
                       ".clusters"))
                stuff (identify-leaf corpus (if (:strong options) :strong :single))]
            (pprint stuff wrtr)))))

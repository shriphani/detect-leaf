(ns detect-leaf.core
  (:require [detect-leaf.utils :as utils]
            [org.bovinegenius [exploding-fish :as uri]])
  (:use [clj-xpath.core :only [$x $x:node $x:node+ $x:text+]])
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
              updated-corpus (merge corpus {(:url picked-url)
                                            body})
              extracted-links (and
                               body
                               (extract body picked-url (clojure.set/union
                                                         (set
                                                          (map :url url-queue))
                                                         (set visited))))]
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
  [start-url]
  (random-sample [{:url start-url}]
                 (set [])
                 extract-in-domain-links
                 #(<= 100 %)
                 {}))

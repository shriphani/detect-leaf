(ns detect-leaf.enlive-helper
  (:require [net.cgrand.enlive-html :as html])
  (:import (org.htmlcleaner HtmlCleaner DomSerializer CleanerProperties)
           (org.w3c.dom Document)))

(defn options->properties
  "Sets the desired properties on the
   HTMLcleaner object"
  [options props]
  (do (doseq [[k v] options]
        (cond (= k :prune-tags)
              (.setPruneTags props v)
              
              (= k :omit-comments)
              (.setOmitComments props v)

              :else
              (throw (Throwable. "Unsupported Option"))))
      props))

(defn process-page
  [page-src options]
  (let [cleaner   (new HtmlCleaner)
        props     (.getProperties cleaner)
        _         (options->properties options props)
        processed (.clean cleaner page-src)]
    (str
     "<"
     (.getName processed)
     ">"
     (.getInnerHtml cleaner processed)
     "</"
     (.getName processed)
     ">")))

(defn html-resource-steroids
  "A more flexible html-resource - inspired by
   html-cleaner's options. We invoke clj-xpath"
  [text & options]
  (let [processed (process-page text
                                (partition 2 options))]
    (-> processed
        java.io.StringReader.
        (html/html-resource))))

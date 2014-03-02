;;;; Soli Deo Gloria
;;;; Author : spalakod@cs.cmu.edu

(ns detect-leaf.utils
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clj-time.core :as time]
            [org.bovinegenius [exploding-fish :as uri]])
  (:use [clojure.tools.logging :only (info error)]
        [clj-logging-config.log4j]))

(defn global-logger-config
  []
  (set-logger!
   :level :debug
   :out   (org.apache.log4j.FileAppender.
           (org.apache.log4j.EnhancedPatternLayout.
            org.apache.log4j.EnhancedPatternLayout/TTCC_CONVERSION_PATTERN)
           "logs/crawl.log"
           true)))

(defn get-and-log
  ([a-link]
     (get-and-log a-link {}))
  ([a-link info]
     (try (-> (client/get a-link) :body)
          (catch Exception e
            (do (error :fetch-failed info)
                (error :url a-link)
                (error (.getMessage e)))))))

(defn reset
  [an-atom value]
  (swap! an-atom (fn [an-obj] value)))

(defn find-in
  "Args:
    m: map
    key: duh

   Returns:
    key that is somewhere in the nested map"
  [m key]
  (cond (not (keys m))         ; leaf
        nil
        
        (some #{key} (keys m)) ; found it
        (m key)

        :else                  ; search
        (first
         (map
          #(when (map? (m %))
             (find-in
              (m %) key))
          (keys m)))))

;; This set of routines has their arguments reversed
;; so I can use them with swap! and atoms
(defn cons-aux
  [coll x]
  (cons x coll))

;; Reading explorations from a file
(defn read-data-file
  [a-file]
  (-> a-file
      clojure.java.io/reader
      java.io.PushbackReader.
      read))

(defn cluster-urls
  [clusters]
  (reverse
   (sort-by
    count
    (map
     (fn [a-cluster]
       (map
        (fn [x]
          (-> x :url))
        a-cluster))
     clusters))))

(defn tokenize
  "Simplistic english tokenizer"
  [a-string]
  (let [string-split (clojure.string/split a-string #"\s+")]
    (filter
     #(not= % "")
     (map
      (fn [a-token]
        (-> a-token
            (.toLowerCase)))
      string-split))))

(def my-cs (cookies/cookie-store)) ; for removing that SID nonsense

(defn download-with-cookie
  [a-link]
  (try (-> a-link (client/get {:cookie-store my-cs}) :body)
       (catch Exception e nil)))

(defn sayln
  "println that prints to stderr"
  [& stuff]
  (binding [*out* *err*]
    (apply println stuff)))


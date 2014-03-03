;;;; Soli Deo Gloria
;;;; Author : spalakod@cs.cmu.edu

(ns detect-leaf.utils
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [org.bovinegenius [exploding-fish :as uri]])
  (:use [clojure.tools.logging :only (info error)]
        [clj-logging-config.log4j]))

(defn distinct-by-key
 [coll k]
 (reduce
  (fn [acc v]
    (if (some #{(v k)} (set (map #(k %) acc)))
      acc
      (cons v acc)))
  []
  coll))

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


(ns detect-leaf.features
  "Features for this classifier"
  (:require [clojure.set :as clj-set]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(defn text->cleaned-tokens
  [a-document]
  (let [tokens (string/split a-document #"\s+|\p{Punct}|\d+")]
    (map string/lower-case tokens)))

(defn language-model
  [a-document]
  (let [tokens (text->cleaned-tokens a-document)]
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

(defn compute-features
  [{anchor-text :anchor-text
    url :url
    body :body
    label :label}]
  (let [anchor-text-language-model (language-model anchor-text)

        body-text (-> body
                      java.io.StringReader.
                      html/html-resource
                      (html/select [:html])
                      first
                      html/text)

        body-language-model (language-model body-text)]
    [(double
      (jaccard-sim
       anchor-text-language-model
       body-language-model))
     (count anchor-text-language-model)
     (if label 1 0)]))

(defn compute-features-csv
  [x]
  (->> x compute-features (string/join ",")))

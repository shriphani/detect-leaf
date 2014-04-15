(ns detect-leaf.train-svm
  (:require [clojure.java.io :as io]
            [detect-leaf.corpus :as corpus]
            [detect-leaf.features :as features])
  (:use [svm.core]))

(defn build-train-file
  [option filename]
  (with-open [wrtr (io/writer filename)]
   (binding [*out* wrtr]
     (let [xs (if (= option :test)
                (corpus/read-test-corpus)
                (corpus/read-train-corpus))]
       (doseq [x xs]
         (println (features/compute-features-libsvm x)))))))

(def build-train-svm (fn [f] (build-train-file :train f)))

(def build-test-svm (fn [f] (build-train-file :test f)))

(defn train
  [train-file]
  (let [dataset (read-dataset train-file)
        model (train-model dataset)]
    (write-model model (str train-file ".model"))))

(defn evaluate
  [model-file test-file]
  (let [model (read-model model-file)
        data  (read-dataset test-file)]
    (reduce
     (fn [acc [label pred]]
       (merge-with + acc (if (= label pred) {:success 1} {:fail 1})))
     {}
     (map
      (fn [[label pt]]
        [label (int (predict model pt))])
      data))))

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

(defn train-svm
  [train-file]
  (let [dataset (read-dataset train-file)
        model (train-model dataset :kernel-type (:linear kernel-types))]
    (write-model model (str train-file ".model"))))

(defn evaluate
  "Return stats for a set"
  [model-file test-file]
  (let [{s :success f :fail fp :false-pos fns :false-neg}
        (let [model (read-model model-file)
              data  (read-dataset test-file)]
          (reduce
           (fn [acc [label pred]]
             (merge-with + acc (if (= label pred)
                                 {:success 1}
                                 {:fail 1
                                  :false-neg
                                  (if (pos? label)
                                    1
                                    0)
                                  :false-pos
                                  (if (pos? label)
                                    0
                                    1)})))
           {}
           (map
            (fn [[label pt]]
              [label (int (predict model pt))])
            data)))]
    {:success s
     :fail f
     :accuracy (double (/ s (+ s f)))
     :false-pos fp
     :false-neg fns}))

(defn evaluate-set
  "Return dataset and values"
  [model-file test-file]
  (let [model (read-model model-file)
        data (read-dataset test-file)]
    (filter
     (fn [[i label pred]]
       (not= label (int pred)))
     (map
      (fn [[[label x] i]]
        [i label (predict model x)])
      (map
       vector
       data
       (range
        (count data)))))))

(defn train-and-test
  [model-prefix]
  (let [train-file (str model-prefix ".train.libsvm")
        test-file  (str model-prefix ".test.libsvm")]
    (do (build-train-svm train-file)
        (build-test-svm test-file)
        (let [model (train-svm train-file)
              train-accuracy (evaluate model train-file)
              test-accuracy  (evaluate model test-file)]
          {:model-file model
           :train-accuracy train-accuracy
           :test-accuracy test-accuracy}))))

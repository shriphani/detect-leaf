(ns detect-leaf.train
  "Classifier training specific code"
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [detect-leaf.corpus :as corpus]
            [detect-leaf.features :as features])
  (:use [clj-ml.classifiers]
        [clj-ml.data]
        [clj-ml.io]
        [clj-ml.utils]))

(defn generate-arff-file
  ([num-features]
     (generate-arff-file num-features "train.arff"))
  ([num-features filename]
     (with-open [wrtr (io/writer filename)]
       (binding [*out* wrtr]
         (let [xs (corpus/read-train-corpus)
               labels (map str "ABCDEFGHIJKLMNOPQRSTUVWXYZ")]
           (println "@RELATION discussion")
           (println)
           (doseq [i (range num-features)]
             (println "@ATTRIBUTE " (nth labels i) " NUMERIC"))
           (println)
           (println "@ATTRIBUTE class {1, 0}")
           (println)
           (println "@DATA")
           (doseq [x xs]
             (println (features/compute-features-csv x))))))))

(defn train
  [train-arff-file class-attr]
  (let [dataset (load-instances :arff (str
                                       (.toURI
                                        (java.io.File. train-arff-file))))
        classifier (make-classifier :decission-tree :c45)

        bin-filename (str (string/replace train-arff-file #".arff" "")
                          "-classifier.bin")]
    (do
      (dataset-set-class dataset class-attr)
      (serialize-to-file
       (classifier-train classifier
                         dataset)
       bin-filename))))
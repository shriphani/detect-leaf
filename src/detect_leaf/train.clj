(ns detect-leaf.train
  "Classifier training specific code"
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [detect-leaf.corpus :as corpus]
            [detect-leaf.features :as features]))

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

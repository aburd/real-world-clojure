(ns real-world-clojure.utils.map)

(defn filter-nil
  [record]
  (select-keys 
    record 
    (for [[k v] record :when (not (nil? v))] k)))

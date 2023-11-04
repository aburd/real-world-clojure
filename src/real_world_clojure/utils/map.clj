(ns real-world-clojure.utils.map
  (:require [real-world-clojure.utils.symbol :refer [kebab-to-camel]]))

(defn filter-nil
  [record]
  (select-keys
   record
   (for [[k v] record :when (not (nil? v))] k)))


(defn map-keys
  [m f]
  (reduce-kv 
    (fn [m k v] 
      (assoc m (f k) (if (map? v) (map-keys v f) v))) 
    {} 
    m))

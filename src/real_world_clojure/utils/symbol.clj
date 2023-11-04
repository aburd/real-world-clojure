(ns real-world-clojure.utils.symbol
  (:require [clojure.string :as s]))

(defn kebab-to-camel
  [sym]
  (-> sym
      str
      (subs 1)
      (s/replace #"-(.)" #(.toUpperCase (%1 1)))
      keyword))

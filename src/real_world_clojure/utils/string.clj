(ns real-world-clojure.utils.string
  (:require [clojure.string :as s]))

(defn sluggify
  [title]
  (-> title
      (s/replace #"\s" "-")
      s/lower-case))

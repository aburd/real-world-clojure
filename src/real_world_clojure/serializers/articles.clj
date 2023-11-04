(ns real-world-clojure.serializers.articles
  (:require [real-world-clojure.serializers.profiles :as s-profiles]))

(def article-shape
  [:slug
   :title
   :description
   :body
   :created-at
   :updated-at
   :tag-list
   :favorited
   :favorites-count])

(defn inner [article-record tag-records]
  (-> article-record
      (select-keys article-shape)
      (assoc :author (s-profiles/inner article-record))
      (assoc :tag-list (map :label tag-records))))

(defn one
  [article-record tag-records]
  {:article (inner article-record tag-records)})

(defn many [record-tuples]
  {:articles (map (partial apply inner) record-tuples)})

; (def record-tuples 
;   [[{:slug "yo" :title "title" :foo "bar"} {:username "let's go baby"}]
;    [{:slug "yee" :title "title" :foo "bar"} {:username "let's go baby"}]
;    [{:slug "yaw" :title "title" :foo "bar"} {:username "let's go baby"}]])

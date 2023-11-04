(ns real-world-clojure.serializers.articles
  (:require [real-world-clojure.serializers.profiles :as s-profiles]))

(def article-shape
  [:slug
   :title
   :description
   :body
   :tagList
   :createdAt
   :updatedAt
   :favorited
   :favoritesCount])

(defn inner [article-record author-record]
  (-> article-record
                (select-keys article-shape)
                (assoc :author (s-profiles/inner author-record))))

(defn one
  [article-record author-record]
  {:article (inner article-record author-record)})

(defn many [record-tuples]
  {:articles (map (partial apply inner) record-tuples)})

; (def record-tuples 
;   [[{:slug "yo" :title "title" :foo "bar"} {:username "let's go baby"}]
;    [{:slug "yee" :title "title" :foo "bar"} {:username "let's go baby"}]
;    [{:slug "yaw" :title "title" :foo "bar"} {:username "let's go baby"}]])

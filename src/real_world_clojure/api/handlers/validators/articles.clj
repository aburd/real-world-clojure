(ns real-world-clojure.api.handlers.validators.articles
  (:require [clojure.spec.alpha :as s]
            [real-world-clojure.utils.string :refer [non-zero-s?]]))

(s/def :articles/body string?)
(s/def :articles/title string?)
(s/def :articles/description string?)
(s/def :articles/tagList (s/coll-of string? :into #{}))
(s/def :articles/article (s/keys :req-un [:articles/title]
                                 :opt-un [:articles/body :articles/description :articles/tagList]))

(s/def :articles/tag (s/and string? non-zero-s?))
(s/def :articles/author (s/and string? non-zero-s?))
(s/def :articles/favorited (s/and string? non-zero-s?))
(s/def :articles/list-articles-params (s/keys :opt-un [:articles/tag
                                                       :articles/author
                                                       :articles/favorited
                                                       :common/limit
                                                       :common/offset]))

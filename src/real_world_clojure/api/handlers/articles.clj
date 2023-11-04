(ns real-world-clojure.api.handlers.articles
  (:require [compojure.core :refer :all]
            [real-world-clojure.api.responses :refer [ok]]
            [real-world-clojure.db.articles :as db-articles]))

(defn list-articles
  [{{:keys [tag author favorited limit offset]
     :or {tag nil
          author nil
          favorited nil
          limit 20
          offset 0}}
    :params :as req}] 
  (ok {:tag tag :author author :fav favorited :lim limit :off offset}))

(defroutes api-routes-articles
  (GET "/" [] list-articles)
  (POST "/" [] "creating article")
  (GET "/:slug" [] "getting article by slug")
  (PUT "/:slug" [] "updating article by slug")
  (DELETE "/:slug" [] "updating article by slug")
  (GET "/:slug/comments" [] "gettings comment on article")
  (POST "/:slug/comments" [] "adding comment to article")
  (DELETE "/:slug/comments/:id" [] "deleting comment from article")
  (POST "/:slug/favorite" [] "favoriting article")
  (DELETE "/:slug/favorite" [] "unfavoriting article")
  (GET "/feed" [] "feed articles"))

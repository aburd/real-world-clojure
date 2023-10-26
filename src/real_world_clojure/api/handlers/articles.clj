(ns real-world-clojure.api.handlers.profiles
  (:require [compojure.core :refer :all]))

(defroutes api-routes-articles
  (GET "/" [] "listing articles")
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

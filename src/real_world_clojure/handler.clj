(ns real-world-clojure.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (context 
    "/api" 
    [] 
    (context
      "/articles"
      []
      (defroutes api-articles-routes
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
        (GET "/feed" [] "feed articles")))
    (context
      "/profiles"
      []
      (defroutes api-profiles-routes
        (GET "/:username" [req] "getting profile with username")
        (POST "/:username/follow" [] "following user")
        (DELETE "/:username/follow" [] "unfollowing user")))
    (context
      "/tags"
      []
      (defroutes api-tags-routes
        (GET "/" [] "getting tags")))
    (context
      "/user"
      []
      (defroutes api-user-routes
        (GET "/" [] "get current user")
        (PUT "/" [] "update user")))
    (context 
      "/users" 
      [] 
      (defroutes api-users-routes
        (POST "/" [] "registration")
        (POST "/login" [] "logging in"))))
  (route/not-found "Not Found"))

(def app
  (-> 
    (handler/api (wrap-defaults app-routes site-defaults))
    middleware/wrap-json-body
    middleware/wrap-json-response))

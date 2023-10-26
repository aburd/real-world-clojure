(ns real-world-clojure.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware]
            [real-world-clojure.api.handlers.articles :refer [api-routes-articles]]
            [real-world-clojure.api.handlers.profiles :refer [api-routes-profiles]]
            [real-world-clojure.api.handlers.tags :refer [api-routes-tags]]
            [real-world-clojure.api.handlers.user :refer [api-routes-user]]
            [real-world-clojure.api.handlers.users :refer [api-routes-users]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (context 
    "/api" 
    [] 
    (context "/articles" [] api-routes-articles)
    (context "/profiles" [] api-routes-profiles)
    (context "/tags" [] api-routes-tags)
    (context "/user" [] api-routes-user)
    (context "/users" [] api-routes-users)) 
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes site-defaults)
    handler/api
    middleware/wrap-json-body
    middleware/wrap-json-response))

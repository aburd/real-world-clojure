(ns real-world-clojure.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware]
            [ring.middleware.reload :refer [wrap-reload]]
            [real-world-clojure.api.handlers.articles :refer [api-routes-articles]]
            [real-world-clojure.api.handlers.profiles :refer [api-routes-profiles]]
            [real-world-clojure.api.handlers.tags :refer [api-routes-tags]]
            [real-world-clojure.api.handlers.users :refer [api-routes-users api-routes-user]]
            [real-world-clojure.api.responses :refer [invalid]]
            [real-world-clojure.api.auth :refer [extract-auth-user]]))

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

(defn wrap-auth [handler]
  (fn [{:keys [headers auth-config] :as req}]
    (let [auth-header (get headers "authorization")
          user (extract-auth-user auth-header auth-config)]
      (handler (if (some? user)
                 (assoc req :authorized? true :user user)
                 (assoc req :authorized? false :user nil))))))

(defn wrap-config [handler]
  (fn [req]
    (handler (assoc req :auth-config {:privkey "keys/auth_privkey.pem"
                                      :pubkey "keys/auth_pubkey.pem"
                                      :passphrase "password"}))))

(defn wrap-validation [handler]
  (fn [req]
    (try
      (handler req)
      (catch clojure.lang.ExceptionInfo e
        (invalid (ex-data e))))))

(def app
  (-> app-routes
      handler/api
      wrap-validation
      middleware/wrap-json-response
      (middleware/wrap-json-body {:keywords? true})
      (wrap-defaults (assoc site-defaults :security {:anti-forgery false}))
      wrap-auth
      wrap-config
      wrap-reload))

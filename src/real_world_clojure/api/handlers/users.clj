(ns real-world-clojure.api.handlers.users
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.serializers.users :as s-users]
            [real-world-clojure.api.responses :refer [ok forbidden not-found]]
            [real-world-clojure.api.auth :as auth]))

(defn create-user-from-params
  [{:keys [username password email]} auth-config]
  (let [password-hash (hs/encrypt password)
        user (db-users/create-user-with-profile
              {:email email :password-hash password-hash}
              {:username username})
        credentials {:email email :password password}
        token (auth/create-auth-token credentials auth-config)]
    (assoc user :token token)))

(defn handle-registration
  [{{:keys [user]} :body :keys [auth-config]}]
  (let [{:keys [email password]} user
        existing-user (db-users/get-user-by "email" email)
        new-user (if (some? existing-user)
                   existing-user
                   (create-user-from-params user auth-config))]
    (ok (s-users/one new-user))))

(defn handle-login
  [{{credentials :user} :body auth-config :auth-config}]
  (let [user (auth/auth-user credentials)]
    (if user
      (do
        (auth/create-auth-token credentials auth-config)
        (ok (s-users/one (db-users/get-user (:user-id user)))))
      (forbidden))))

(defn handle-get-user
  [{:keys [authorized? user headers]}]
  (if authorized?
    (ok (s-users/one (db-users/get-user (:user-id user))))
    (not-found)))

(defn handle-update-user
  [{:keys [user body] :as req}]
  (println :user user)
  (if user
    (ok (s-users/one (db-users/update-user-and-profile (:user-id user) (:user body))))
    (forbidden)))

(defroutes api-routes-users
  (POST "/" [] handle-registration)
  (POST "/login" [] handle-login))

(defroutes api-routes-user
  (GET "/" [] handle-get-user)
  (PUT "/" [] handle-update-user))

; (db-users/get-user 63)

; (defn hello 
;   [person & {:keys [greeting is-excited] 
;              :or {greeting "Hello" is-excited false}}]
;   (println (format "%s, %s%s" greeting person (if is-excited "!" "."))))

; (hello "Aaron" :greeting "Yo" :is-excited true)


(ns real-world-clojure.api.handlers.users
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.api.responses :refer [ok forbidden]]
            [real-world-clojure.api.auth :as auth]))

(defn create-user-from-params
  [{:keys [username password email]}]
  (let [password-hash (hs/encrypt password)]
    (db-users/create-user-with-profile
      {:email email :password-hash password-hash :token "implement-me"}
      {:username username})))

(defn handle-registration
  [{{:keys [user]} :body}]
  (let [user (or 
               (db-users/get-user-by-email (:email user))
               (create-user-from-params user))]
    (ok user)))

(defn handle-login
  [{{credentials :user auth-config :auth-config} :body}]
  (let [user (auth/auth-user credentials)]
    (if user
      (do
        (db-users/update-user (:id user) {:token (auth/create-auth-token credentials auth-config)})
        (ok (db-users/get-user (:id user))))
      (forbidden))))

(defroutes api-routes-users
  (POST "/" [] handle-registration)
  (POST "/login" [] handle-login))

; (db-users/get-user 63)

; (defn hello 
;   [person & {:keys [greeting is-excited] 
;              :or {greeting "Hello" is-excited false}}]
;   (println (format "%s, %s%s" greeting person (if is-excited "!" "."))))

; (hello "Aaron" :greeting "Yo" :is-excited true)


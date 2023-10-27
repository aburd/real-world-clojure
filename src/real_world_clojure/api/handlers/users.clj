(ns real-world-clojure.api.handlers.users
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]
            [real-world-clojure.api.responses :refer [ok]]))

(defn create-user-from-params
  [{:keys [username password email]}]
  (let [password-hash (hs/encrypt password)]
    (db-users/create-user-with-profile
      {:email email :password-hash password-hash :token "implement-me"}
      {:username username})))

(defn handle-registration
  [{{:keys [user]} :body}]
  (println "USER::::"  user)
  (let [user (or 
               (db-users/get-user-by-email (:email user))
               (create-user-from-params user))]
    (ok user)))

(defroutes api-routes-users
  (POST "/" [] handle-registration)
  (POST "/login" [] "logging in"))

; (db-users/get-user 63)

; (defn hello 
;   [person & {:keys [greeting is-excited] 
;              :or {greeting "Hello" is-excited false}}]
;   (println (format "%s, %s%s" greeting person (if is-excited "!" "."))))

; (hello "Aaron" :greeting "Yo" :is-excited true)


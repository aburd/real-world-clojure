(ns real-world-clojure.api.handlers.users
  (:require [buddy.hashers :as hs]
            [compojure.core :refer :all]
            [real-world-clojure.db.users :as db-users]))

(defn create-user
  [ds user]
  (hs/encrypt (:password user)))

(defn get-user
  [req] 
  (db-users/get-user))

(defn update-user 
  [req] 
  (db-users/get-user))

(defroutes api-routes-users
  (POST "/" [] "registration")
  (POST "/login" [] "logging in"))

; (db-users/get-user 63)

; (defn hello 
;   [person & {:keys [greeting is-excited] 
;              :or {greeting "Hello" is-excited false}}]
;   (println (format "%s, %s%s" greeting person (if is-excited "!" "."))))

; (hello "Aaron" :greeting "Yo" :is-excited true)


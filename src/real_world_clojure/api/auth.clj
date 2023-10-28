(ns real-world-clojure.api.auth
  (:require [buddy.sign.jws :as jws]
            [buddy.core.keys :as ks]
            [buddy.hashers :as hs]
            [clj-time.core :as t]
            [clj-time.coerce :as t-coerce]
            [clojure.java.io :as io]
            [real-world-clojure.db.users :as db-users]
            [clojure.data.json :as json]))

(defn- pkey 
  [{:keys [privkey passphrase] :as auth-config}]
  (ks/private-key (io/resource privkey) passphrase))

(defn- pubkey 
  [{:keys [pubkey] :as auth-config}]
  (ks/public-key (io/resource pubkey)))

(defn auth-user
  [{:keys [email password] :as credentials}]
  (let [user (db-users/get-full-user-by-email email)]
    (println user)
    (when user
      (when (hs/check password (:password-hash user))
        (dissoc user :password-hash)))))

(defn expiration []
  (-> (t/plus (t/now) (t/days 1))
      (t-coerce/to-epoch)))

(defn create-auth-token
  [credentials auth-conf]
  (let [user (auth-user credentials)]
    (when (some? user)
      (jws/sign 
        (json/write-str user)
        (pkey {:privkey "keys/auth_privkey.pem" :passphrase "password"}) 
        {:alg :rs256 :exp (expiration)}))))

(defn decode-auth-token
  [token auth-config]
  (->> (jws/decode token (pubkey auth-config) {:alg :rs256})
       (map char)
       (apply str)
       json/read-str))

; (decode-auth-token
;   (create-auth-token 
;     {:email "aaron.burdick@protonmail.com" :password "password"}
;     {:privkey "keys/auth_privkey.pem" :passphrase "password"})
;   {:pubkey "keys/auth_pubkey.pem"})

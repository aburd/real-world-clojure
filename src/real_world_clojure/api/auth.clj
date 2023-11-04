(ns real-world-clojure.api.auth
  (:require [buddy.sign.jws :as jws]
            [buddy.core.keys :as ks]
            [buddy.hashers :as hs]
            [clj-time.core :as t]
            [clj-time.coerce :as t-coerce]
            [clojure.java.io :as io]
            [real-world-clojure.db.users :as db-users]
            [clojure.data.json :as json]
            [clojure.string :refer [split]]))

(defn- pkey
  [{:keys [privkey passphrase] :as auth-config}]
  (ks/private-key (io/resource privkey) passphrase))

(defn- pubkey
  [{:keys [pubkey] :as auth-config}]
  (ks/public-key (io/resource pubkey)))

(defn auth-user
  [{:keys [email password] :as credentials}]
  (let [user (db-users/get-user-by "email" email)]
    (when user
      (when (hs/check password (:password-hash user))
        (dissoc user :password-hash)))))

(defn expiration []
  (-> (t/plus (t/now) (t/days 1))
      (t-coerce/to-epoch)))

(defn create-auth-token
  [credentials auth-config]
  (let [user (auth-user credentials)]
    (when (some? user)
      (let [token (jws/sign
                   (json/write-str {:id (:user-id user)})
                   (pkey auth-config)
                   {:alg :rs256 :exp (expiration)})]
        (db-users/update-user (:user-id user) {:token token})
        token))))

(defn decode-auth-token
  [token auth-config]
  (->> (jws/decode token (pubkey auth-config) {:alg :rs256})
       (map char)
       (apply str)
       json/read-str))

(defn extract-auth-user
  [auth-header auth-config]
  (when (some? auth-header)
    (let [token (last (split auth-header #" "))]
      (try
        (let [user (decode-auth-token token auth-config)]
          (db-users/get-user-by "id" (get user "id")))
        (catch Exception e nil)))))

; (def auth-config {:pubkey "keys/auth_pubkey.pem" :privkey "keys/auth_privkey.pem" :passphrase "password"})
; (def dummy-token "Token eyJhbGciOiJSUzI1NiJ9.eyJmb28iOiJiYXIifQ.EPRGP8apUa7c1ppxhBfCA-JubbiPtnSUC3O3U0actzeIrNnMzqMFHQj8Dh6S9s-Czu_sA-U0Z-RlLMVPBVXWD0fZiID0rzMOr23a9A0WPgrtD_VvncklAoYGsF0d4JdWziJbq6ggkvZqckbExiWHPEP8L16l_IYkhyosFHiaGdO-3TI3fL3r31cx36O1AdsZ-u958yJ-6Pfc-04Y4s0K0KWgpLiIUQTj1ekuoYvCbH_jCRdc5W-MxCi5FLdXk6EzW88T6p42cOCOdGyfF36osAjyj05aZZ7vcOwnAM2wFIaX8uG4oUk-qe5ThyZ_OR_gVT474y3MtzaVrs52Md3_ig")
; (def bad-dummy-token "")
; (extract-auth-token dummy-token auth-config)
; (extract-auth-token bad-dummy-token auth-config)

; (decode-auth-token
; (create-auth-token 
;   {:email "aaron.burdick@protonmail.com" :password "password"}
;   {:privkey "keys/auth_privkey.pem" :passphrase "password"})
;   {:pubkey "keys/auth_pubkey.pem"})

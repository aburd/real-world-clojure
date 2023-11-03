(ns real-world-clojure.serializers.profiles)

(defn one
  [{:keys [email username bio image following] :as record}]
  {:user {:email email :username username :bio bio :image image :following following}})

(ns real-world-clojure.serializers.users)

(defn inner [record]
  (select-keys record [:email :token :username :bio :image]))

(defn one
  [record]
  {:user (inner record)})

(defn many
  [users]
  (map one users))

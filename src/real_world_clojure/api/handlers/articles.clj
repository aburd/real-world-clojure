(ns real-world-clojure.api.handlers.articles
  (:require [compojure.core :refer :all]
            [real-world-clojure.api.responses :refer [ok]]
            [real-world-clojure.db.articles :as db-articles]
            [real-world-clojure.db.profiles :as db-profiles]
            [real-world-clojure.serializers.articles :as s-articles]))

(defn list-articles
  [{{:keys [tag author favorited limit offset]
     :or {tag nil
          author nil
          favorited nil
          limit 20
          offset 0}}
    :params :as req}] 
  (ok (s-articles/many (db-articles/query-articles {:tag tag
                                                    :author author
                                                    :favorited favorited
                                                    :limit limit
                                                    :offset offset}))))

(defn get-article-by-slug
  [{{:keys [slug]} :params user :user}]
  (let [article (db-articles/get-article-by "slug" slug)
        author (db-profiles/get-profile-by "id" (:author-id article) (:id user))]
    (ok (s-articles/one article author))))
    

(defroutes api-routes-articles
  (GET "/" [] list-articles)
  (POST "/" [] "creating article")
  (GET "/:slug" [] get-article-by-slug)
  (PUT "/:slug" [] "updating article by slug")
  (DELETE "/:slug" [] "updating article by slug")
  (GET "/:slug/comments" [] "gettings comment on article")
  (POST "/:slug/comments" [] "adding comment to article")
  (DELETE "/:slug/comments/:id" [] "deleting comment from article")
  (POST "/:slug/favorite" [] "favoriting article")
  (DELETE "/:slug/favorite" [] "unfavoriting article")
  (GET "/feed" [] "feed articles"))

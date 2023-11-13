(ns real-world-clojure.api.handlers.articles
  (:require [compojure.core :refer :all]
            [real-world-clojure.api.responses :refer [ok]]
            [real-world-clojure.api.params :refer [to-int]]
            [real-world-clojure.api.handlers.validators.core :refer [validate parse-params]]
            [real-world-clojure.api.handlers.validators.articles]
            [real-world-clojure.db.articles :as db-articles]
            [real-world-clojure.db.profiles :as db-profiles]
            [real-world-clojure.db.tags :as db-tags]
            [real-world-clojure.serializers.articles :as s-articles]))

(defn list-articles
  [{{:keys [tag author favorited limit offset]
     :or {tag nil
          author nil
          favorited nil
          limit 20
          offset 0}}
    :params 
    user :user
    :as req}] 
  (let [articles (db-articles/query-articles {:tag tag
                                              :author author
                                              :favorited favorited
                                              :limit limit
                                              :offset offset
                                              :user-id (:id user)})
        article-ids (map :article-id articles)
        tag-groups (map (partial db-tags/get-tags-by "article_id") article-ids)] 
    (ok (s-articles/many (map vector articles tag-groups)))))

(defn get-article-by-slug
  [{{:keys [slug]} :params user :user}]
  (let [article (db-articles/get-article-by "slug" slug (:id user))
        tags (db-tags/get-tags-by "article_id" (:id article))]
    (ok (s-articles/one article tags))))

(defn create-article
  [{{:keys [article]} :body user :user}]
  (let [{:keys [title description body tagList]} article
        user-id (:id user)
        record (db-articles/create-article 
                 user-id
                 {:tags tagList :title title :description description :body body})
        article (db-articles/get-article-by "id" (:id record) user-id)
        tags (db-tags/get-tags-by "article_id" (:id article))]
    (ok (s-articles/one article tags)))) 

(defn favorite-article
  [{{:keys [slug]} :params user :user}]
  (let [user-id (:id user)
        profile (db-profiles/get-profile-by-user-id user-id)
        record (db-articles/favorite-article (:id profile) slug)
        article (db-articles/get-article-by "slug" slug user-id)
        tags (db-tags/get-tags-by "article_id" (:id article))]
    (ok (s-articles/one article tags))))
    
(defroutes api-routes-articles
  (GET "/" [] (validate 
                (parse-params list-articles {:limit to-int
                                             :offset to-int}) 
                {:params-key :articles/list-articles-params}))
  (POST "/" [] create-article)
  (GET "/:slug" [] get-article-by-slug)
  (PUT "/:slug" [] "updating article by slug")
  (DELETE "/:slug" [] "updating article by slug")
  (GET "/:slug/comments" [] "gettings comment on article")
  (POST "/:slug/comments" [] "adding comment to article")
  (DELETE "/:slug/comments/:id" [] "deleting comment from article")
  (POST "/:slug/favorite" [] favorite-article)
  (DELETE "/:slug/favorite" [] "unfavoriting article")
  (GET "/feed" [] "feed articles"))

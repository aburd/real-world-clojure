create table if not exists favorite_articles(
  id int generated always as identity primary key,
  profile_id int not null,
  article_id int not null,

  foreign key(profile_id)
    references profiles(id),
  foreign key(article_id)
    references articles(id),
  unique(profile_id, article_id)
);

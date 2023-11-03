create table if not exists articles(
  id int generated always as identity primary key,
  slug text not null,
  title text not null,
  description text not null default '',
  body text not null default '',
  author_id int not null,
  created_at timestamp default current_date not null,
  updated_at timestamp default current_date not null,

  foreign key(author_id)
    references profiles(id),
  unique(author_id, slug)
);
--;;
create table if not exists tags(
  id int generated always as identity primary key,
  label text not null,
  article_id int not null,

  foreign key(article_id)
    references articles(id)
);

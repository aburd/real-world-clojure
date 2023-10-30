create table if not exists follows(
  id int generated always as identity primary key,
  follower_id int not null,
  following_id int not null,
  foreign key(follower_id)
    references users(id),
  foreign key(following_id)
    references users(id),
  unique(follower_id, following_id)
);

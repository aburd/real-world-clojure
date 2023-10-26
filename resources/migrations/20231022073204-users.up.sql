create table if not exists users(
  id int generated always as identity primary key,
  email text not null unique,
  password_hash text not null,
  token text,
  created_at timestamp default current_date not null,
  last_login timestamp
);
--;;
create table if not exists profiles(
  id int generated always as identity primary key,
  user_id bigint not null,
  username text not null unique,
  bio text,
  image text,
  foreign key(user_id)
    references users(id)
    on delete cascade
);

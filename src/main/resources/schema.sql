create table if not exists name
(
    id                 integer not null,
    first_name         varchar(255),
    last_name          varchar(255),
    date_of_birth      date,
    date_of_death      date,
    height             float8 check (height >= 0),
    number_of_children integer check (number_of_children >= 0),
    primary key (id)
);

create table if not exists film
(
    id           integer not null,
    name         varchar(255),
    director_id  integer,
    duration     integer check (duration >= 0),
    description  text,
    rating       float8 check (rating >= 0 AND rating <= 10),
    release_date date,
    primary key (id),
    constraint fk_film_director foreign key (director_id) references name (id)
);

create table if not exists film_actors
(
    film_id   integer not null,
    actors_id integer not null,
    primary key (film_id, actors_id),
    foreign key (film_id) references film (id),
    foreign key (actors_id) references name (id)
);
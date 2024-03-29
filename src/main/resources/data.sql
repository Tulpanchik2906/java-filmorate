CREATE TABLE IF NOT EXISTS genre (
        id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS rating_mpa (
        id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar NOT NULL,
        description varchar
);

CREATE TABLE IF NOT EXISTS films (
        id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar NOT NULL,
        description varchar NOT NULL,
        release_date date,
        duration integer,
        rating_mpa_id integer,
        CONSTRAINT fk_rating_mpa FOREIGN KEY(rating_mpa_id)
		  REFERENCES rating_mpa(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users (
        id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
				email varchar NOT NULL,
				login varchar NOT NULL,
				name varchar,
				birthday date
);

CREATE TABLE IF NOT EXISTS likes (
    user_id INTEGER,
    film_id INTEGER,
    CONSTRAINT fk_user FOREIGN KEY(user_id)
		  REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT fk_film FOREIGN KEY(film_id)
		  REFERENCES films(id) ON DELETE CASCADE,
	PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    initiator_id INTEGER,
    acceptor_id INTEGER,
	status boolean,
	CONSTRAINT fk_user_initiator FOREIGN KEY(initiator_id)
		  REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT fk_user_acceptor FOREIGN KEY(acceptor_id)
		  REFERENCES users(id) ON DELETE CASCADE,
	PRIMARY KEY (initiator_id,  acceptor_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    genre_id INTEGER,
    film_id INTEGER,
    CONSTRAINT fk_genre FOREIGN KEY(genre_id)
		  REFERENCES genre(id) ON DELETE CASCADE,
	CONSTRAINT fk_film_g FOREIGN KEY(film_id)
		  REFERENCES films(id) ON DELETE CASCADE,
	PRIMARY KEY (genre_id, film_id)
);

-- Справочник жанров
insert into GENRE
    select * from (
        select 1, 'Комедия' union
        select 2, 'Драма' union
        select 3, 'Мультфильм' union
        select 4, 'Триллер' union
        select 5, 'Документальный' union
        select 6, 'Боевик'
    ) GENRE where not exists(select * from GENRE);

-- Справочник Рейтинг MPA
insert into rating_mpa
    select * from (
        select 1, 'G', 'у фильма нет возрастных ограничений' union
        select 2, 'PG', 'детям рекомендуется смотреть фильм с родителями' union
        select 3, 'PG-13', 'детям до 13 лет просмотр не желателен' union
        select 4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого' union
        select 5, 'NC-17', 'лицам до 18 лет просмотр запрещён'
    ) rating_mpa where not exists(select * from rating_mpa);

SELECT * FROM rating_mpa;
COMMIT;

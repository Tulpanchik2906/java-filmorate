package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.RatingMpa;
import ru.yandex.practicum.filmorate.storage.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component("FilmDbStorage")
@Slf4j
public class FilmDao implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Film film) {
        // Сохранить основные поля фильма
        addBasicFieldsByFilm(film);
        // Сохранить список жанров
        addGenresByFilm(film);
        // Сохранить рейтинг mpa
        addRatingMpaByFilm(film);
    }


    @Override
    public void update(Film film) {
        // Обновить основные поля фильма
        updateBasicFieldsByFilm(film);
        // Обновить список жанров
        updateGenresByFilm(film);
        // Обновить рейтинг MPA
        updateRatingMpaByFilm(film);
    }

    @Override
    public void delete(Film film) {
        jdbcTemplate.update("DELETE FROM FILMS WHERE ID = ?", film.getId());
        log.info("Удален фильм с id: {}.", film.getId());
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query("SELECT * FROM FILMS", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(int id) {
        List<Film> films = jdbcTemplate.query(
                "SELECT * FROM FILMS WHERE ID = ?",
                (rs, rowNum) -> makeFilm(rs), id);
        if (films.size() == 0) {
            return null;
        } else {
            return films.get(0);
        }
    }

    @Override
    public void clear() {
        jdbcTemplate.update("DELETE FROM FILMS");
        log.info("Удалены все фильмы.");
    }

    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO LIKES (film_id, user_id) VALUES (?,?)",
                filmId, userId);
        log.info("Добавлен лайк от пользователя с id {} фильму с id {}.",
                userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM LIKES " +
                        "WHERE film_id = ? AND user_id = ?",
                filmId, userId);
        log.info("Удален лайк от пользователя с id {} фильму с id {}.",
                userId, filmId);
    }

    @Override
    public int getLastFilmId() {
        List<Integer> list = jdbcTemplate.queryForList("SELECT ID FROM FILMS " +
                "ORDER BY ID DESC LIMIT 1", Integer.class);
        if (list.isEmpty()) {
            return -1;
        } else {
            return list.get(0);
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int filmId = rs.getInt("id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .genres(getGenres(filmId))
                .mpa(getRatingMpaWithOnlyId(filmId))
                .likeUserIds(getLikes(filmId))
                .build();

    }

    private Set<Genre> getGenres(int film_id) {
        return new TreeSet<>(jdbcTemplate.query("SELECT GENRE.* FROM FILMS\n" +
                        "JOIN FILM_GENRE\n" +
                        "ON FILMS.ID = FILM_GENRE.FILM_ID\n" +
                        "JOIN GENRE \n" +
                        "ON GENRE.ID = FILM_GENRE.GENRE_ID " +
                        "WHERE FILMS.ID = ?",
                new BeanPropertyRowMapper<Genre>(Genre.class), film_id));
    }

    private RatingMpa getRatingMpaWithOnlyId(int film_id) {
        List<RatingMpa> ratingMpa = jdbcTemplate.query("SELECT RATING_MPA.* FROM\n" +
                "FILMS \n" +
                "JOIN RATING_MPA ON\n" +
                "FILMS.RATING_MPA_ID = RATING_MPA.ID " +
                "WHERE FILMS.ID = ?", new BeanPropertyRowMapper<RatingMpa>(RatingMpa.class), film_id);
        if (ratingMpa.size() == 0) {
            return null;
        } else {
            RatingMpa ratingMpaRes = ratingMpa.get(0);
            ratingMpaRes.setDescription(null);
            return ratingMpaRes;
        }
    }

    private Set<Integer> getLikes(int film_id) {
        return new TreeSet<>(jdbcTemplate.queryForList(
                "SELECT LIKES.USER_ID \n" +
                        "FROM LIKES\n" +
                        "JOIN FILMS \n" +
                        "ON FILMS.ID = LIKES.FILM_ID\n" +
                        "WHERE FILMS.ID = ?", Integer.class, film_id));
    }

    private void addBasicFieldsByFilm(Film film) {
        jdbcTemplate.update("INSERT INTO FILMS ( id, name, description, release_date, duration)\n" +
                        "VALUES (?, ?, ?, ?, ?)", film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());

        log.info("В фильме с id: {} добавлены основные поля.", film.getId());
    }

    private void addGenresByFilm(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre_id)\n" +
                        "VALUES (?, ?) ", film.getId(), genre.getId());
            }
        }

        log.info("В фильме с id: {} добавлены жанры.", film.getId());
    }

    private void addRatingMpaByFilm(Film film) {
        if (film.getMpa() != null) {
            jdbcTemplate.update("UPDATE FILMS SET RATING_MPA_ID = ? " +
                    "WHERE id = ?", film.getMpa().getId(), film.getId());

            log.info("В фильме с id: {} добавлен рейтинг mpa.", film.getId());
        }
    }

    private void updateBasicFieldsByFilm(Film film) {
        jdbcTemplate.update("UPDATE FILMS SET name = ?, " +
                        "description = ?, release_date = ?, duration = ? " +
                        "WHERE ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        log.info("В фильме с id: {} обновлены основные поля.", film.getId());
    }

    private void updateGenresByFilm(Film film) {
        // Удалить все жанры
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?",
                film.getId());

        log.info("В фильме с id: {} удалены все жанры.", film.getId());

        addGenresByFilm(film);
    }

    private void updateRatingMpaByFilm(Film film) {
        if (film.getMpa() != null) {
            jdbcTemplate.update("UPDATE FILMS SET RATING_MPA_ID = ? " +
                    "WHERE id = ?", film.getMpa().getId(), film.getId());
        } else {
            jdbcTemplate.update("UPDATE FILMS SET RATING_MPA_ID = NULL " +
                    "WHERE id = ?", film.getId());
        }

        log.info("В фильме с id: {} обновлен рейтинг mpa.", film.getId());
    }
}

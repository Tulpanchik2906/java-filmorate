package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.RatingMpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


@SpringBootTest
@AutoConfigureTestDatabase
public class FilmDaoTest {

    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    @AfterEach
    public void afterEach() {
        filmStorage.clear();
        userStorage.clear();
    }

    @Test
    public void addFilmTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);
        Film saveFilm = filmStorage.getFilmById(film.getId());

        Assertions.assertNotNull(saveFilm);
    }

    @Test
    public void updateFilmTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);
        film.setDescription("Кейт и Лео");
        filmStorage.update(film);
        Film saveFilm = filmStorage.getFilmById(film.getId());

        Assertions.assertNotNull(saveFilm);
        Assertions.assertEquals("Кейт и Лео", saveFilm.getDescription());
    }

    @Test
    public void deleteFilmTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);
        filmStorage.delete(film);
        Film saveFilm = filmStorage.getFilmById(film.getId());

        Assertions.assertNull(saveFilm);
    }

    @Test
    public void findAllFilmTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);
        film.setId(2);
        filmStorage.add(film);

        List<Film> films = filmStorage.findAll();

        Assertions.assertEquals(2, films.size());
    }

    @Test
    public void getFilmByExistIdTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);

        Film saveFilm = filmStorage.getFilmById(film.getId());

        Assertions.assertEquals(film.getName(), saveFilm.getName());
    }

    @Test
    public void getFilmByNoExistIdTest() {

        Film saveFilm = filmStorage.getFilmById(1001);

        Assertions.assertNull(saveFilm);
    }

    @Test
    public void clearFilmsTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);
        film.setId(2);
        filmStorage.add(film);

        List<Film> films = filmStorage.findAll();

        filmStorage.clear();

        Assertions.assertTrue(filmStorage.findAll().isEmpty());
    }

    @Test
    public void addLikeTest() {
        Film film = getAllFieldsFilm();
        film.setLikeUserIds(new TreeSet<>());
        filmStorage.add(film);

        List<User> list = userStorage.findAll();

        filmStorage.addLike(film.getId(), list.get(0).getId());

        Film saveFilm = filmStorage.getFilmById(film.getId());

        Assertions.assertEquals(1, saveFilm.getLikeUserIds().size());
        Assertions.assertTrue(saveFilm.getLikeUserIds().contains(list.get(0).getId()));
    }

    @Test
    public void deleteLikeTest() {
        Film film = getAllFieldsFilm();
        film.setLikeUserIds(new TreeSet<>());
        filmStorage.add(film);

        List<User> list = userStorage.findAll();

        filmStorage.addLike(film.getId(), list.get(0).getId());
        filmStorage.deleteLike(film.getId(), list.get(0).getId());

        Assertions.assertTrue(
                filmStorage.getFilmById(film.getId()).getLikeUserIds().isEmpty());
    }

    @Test
    public void getLastFilmIdTest() {
        Film film = getAllFieldsFilm();
        filmStorage.add(film);
        film.setId(2);
        filmStorage.add(film);

        Assertions.assertEquals(2, filmStorage.getLastFilmId());
    }

    private Film getAllFieldsFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("Титаник");
        film.setDescription("Фильм о любви и затонувшем корабле.");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(194);
        film.setMpa(getMpaForTitanic());

        film.setGenres(getGenresForTitanic());
        film.setLikeUserIds(getLikesForTitanic());


        return film;
    }

    private Set<Genre> getGenresForTitanic() {
        Set<Genre> set = new TreeSet<>();

        Genre drama = new Genre();
        drama.setId(2);
        drama.setName("Драма");

        Genre triller = new Genre();
        triller.setId(4);
        triller.setName("Триллер");

        set.add(drama);
        set.add(triller);

        return set;
    }

    private RatingMpa getMpaForTitanic() {
        RatingMpa ratingMpa = new RatingMpa();
        ratingMpa.setId(3);
        ratingMpa.setName("PG-13");
        ratingMpa.setDescription("детям до 13 лет просмотр не желателен");
        return ratingMpa;
    }

    private Set<Integer> getLikesForTitanic() {

        List<Integer> users = new ArrayList<>();

        User user1 = getAllFieldsUser(1);
        User user2 = getAllFieldsUser(2);

        userStorage.add(user1);
        userStorage.add(user2);

        users.add(user1.getId());
        users.add(user2.getId());

        return new TreeSet<>(users);
    }

    private User getAllFieldsUser(int id) {
        User user = new User();
        user.setId(id);
        user.setName("Name user");
        user.setLogin("Login user");
        user.setEmail("user@yandex.ru");
        user.setBirthday(LocalDate.of(1998, 10, 15));

        return user;
    }

}

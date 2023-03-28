package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private int generatedId = 0;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);

        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }

        return film;
    }


    public Film addFilm(Film film) {
        checkDateFilm(film);
        int id = generateId();
        film.setId(id);
        filmStorage.add(film);
        log.info("Фильм с id = {} успешно добавлен", id);
        return film;
    }

    public Film updateFilm(Film film) {
        checkExistIdForUpdate(film);
        checkDateFilm(film);
        filmStorage.update(film);
        log.info("Фильм с id = {} успешно обновлен", film.getId());
        return film;
    }

    public List<Film> getPopularFilmsByLike(int count) {
        List<Film> films = new ArrayList<>(filmStorage.findAll());

        films.sort(Comparator
                .comparingInt((film) -> film.getLikeUserIds().size()));
        Collections.reverse(films);
        List res = new ArrayList();
        for (int i = 0; i < films.size(); i++) {
            if (i < count) {
                res.add(films.get(i));
            }
        }
        return res;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
        log.info("К фильму с id = {} успешно добавлен лайк " +
                        "от пользователя с id = {}",
                film.getId(), user.getId());
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        filmStorage.deleteLike(filmId, userId);

        log.info("К фильму с id = {} успешно удален лайк " +
                        "от пользователя с id = {}",
                film.getId(), user.getId());
    }

    public void clearFilms() {
        filmStorage.clear();
        generatedId = 0;

        log.info("Список фильмов пуст");
    }


    private void checkDateFilm(Film film) {
        if (film.getReleaseDate() == null) {
            return;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Кино тогда еще не родилось.");
            throw new ValidationException("Кино тогда еще не родилось.");
        }

    }

    private void checkExistIdForUpdate(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            log.error("Фильм с id " + film.getId() + " не существует.");
            throw new NotFoundException("Фильм с id " + film.getId() + " не существует.");
        }
    }

    private int generateId() {
        generatedId++;
        return generatedId;
    }
}

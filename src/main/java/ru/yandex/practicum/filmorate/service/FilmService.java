package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private FilmStorage filmStorage;

    private UserStorage userStorage;

    private int generatedId = 0;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage) {
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
        return film;
    }

    public Film updateFilm(Film film) {
        checkExistIdForUpdate(film);
        checkDateFilm(film);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getPopularFilmsByLike(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilms().values());

        films.sort(Comparator
                .comparingInt((film) -> film.getLikeUserIds().size()));
        Collections.reverse(films);
        List res = new ArrayList();
        for (int i =0;i<films.size();i++){
            if(i<count){
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
            throw new NotFoundException("Пользователь " + filmId + " не найден");
        }
        film.addLike(userId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь " + filmId + " не найден");
        }

        film.deleteLike(userId);
    }

    public void clearFilms() {
        filmStorage.clear();
        generatedId = 0;
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
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " не существует.");
            throw new NotFoundException("Фильм с id " + film.getId() + " не существует.");
        }
    }

    private int generateId() {
        generatedId++;
        return generatedId;
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private FilmStorage filmStorage;

    private int generatedId = 0;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    public List<Film> getFilms() {
        return filmStorage.findAll();
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

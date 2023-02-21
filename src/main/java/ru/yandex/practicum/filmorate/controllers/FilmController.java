package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    Map<Integer, Film> films = new HashMap<>();
    private static int generateId = 0;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        checkRepeatFilmForCreate(film);
        checkDateFilm(film);
        generateId++;
        film.setId(generateId);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        checkExistIdForUpdate(film);
        checkDateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private void checkRepeatFilmForCreate(Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " уже существует.");
            throw new ValidationException("Фильм с id " + film.getId() + " уже существует.");
        }
    }

    private void checkDateFilm(Film film) {
        if(film.getReleaseDate() == null){
            return;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Кино тогда еще не родилось.");
            throw new ValidationException("Кино тогда еще не родилось.");
        }

    }

    private void checkExistIdForUpdate(Film film){
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " не существует.");
            throw new ValidationException("Фильм с id " + film.getId() + " не существует.");
        }
    }
}

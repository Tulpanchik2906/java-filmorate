package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<Integer, Film> films = new HashMap<>();
    private int generateId = 0;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public @ResponseBody Film addFilm(@Valid @RequestBody Film film) {
        checkDateFilm(film);
        generateId++;
        film.setId(generateId);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public @ResponseBody Film updateFilm(@Valid @RequestBody Film film) {
        checkExistIdForUpdate(film);
        checkDateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException exception)
            throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(exception));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception)
            throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(exception));
    }

    public void clearFilms() {
        films.clear();
        generateId = 0;
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
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id " + film.getId() + " не существует.");
            throw new NotFoundException("Фильм с id " + film.getId() + " не существует.");
        }
    }
}

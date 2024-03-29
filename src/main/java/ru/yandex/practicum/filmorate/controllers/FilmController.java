package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable Integer id,
                            @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable Integer id,
                               @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getPopularFilmsByLike(count);
    }


    public void clearFilms() {
        filmService.clearFilms();
    }

}

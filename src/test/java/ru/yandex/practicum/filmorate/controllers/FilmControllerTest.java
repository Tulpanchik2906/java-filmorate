package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

@SpringBootTest
public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void beforeEach(){
        filmController = new FilmController();
    }

    @Test
    public void testGetEmptyFilmList() {
        List<Film> filmList = filmController.getFilms();
    }

    @Test
    public void testGetFilmList() {
        filmController.addFilm(getNewFilm());
        filmController.addFilm(getNewFilm());
        filmController.addFilm(getNewFilm());

        List<Film> filmList = filmController.getFilms();

        Assertions.assertEquals(filmList.size(), 3);
    }

    private Film getNewFilm(){
        Film film = new Film();
        film.setName("Новый фильм");
        film.setDuration(12);

        return film;
    }
}

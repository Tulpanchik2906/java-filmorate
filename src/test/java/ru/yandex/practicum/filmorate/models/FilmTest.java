package ru.yandex.practicum.filmorate.models;


import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class FilmTest {

    private Film film;

    @BeforeEach
    public void beforeEach(){
        film = new Film();
    }

    @Test
    public void testCreateAllField() {
        setAllFieldsFilm();
    }

    @Test
    public void testGetErrorWhenNullName() {
        setAllFieldsFilm();
        film.setName(null);
        film.setName("");
        film.setName("     ");
    }


    private void setAllFieldsFilm(){
        film.setId(1);
        film.setName("Титаник");
        film.setDescription("Фильм о любви и затонувшем корабле.");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(194);
    }

}

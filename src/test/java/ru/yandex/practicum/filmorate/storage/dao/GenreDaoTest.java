package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storage.storage.GenreStorage;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTest {

    @Autowired
    private final GenreStorage genreStorage;

    @Test
    public void testGetGenreByIdExistId(){
        Genre genre = genreStorage.getGenreById(1);
        Assertions.assertEquals(genre.getId(),1);
        Assertions.assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testGetGenreByIdNoExistId(){
        Genre genre = genreStorage.getGenreById(100);
        Assertions.assertNull(genre);
    }

    @Test
    public void testFindALL(){
        List<Genre> genres = genreStorage.findAll();
        Assertions.assertEquals(6, genres.size());
    }

}

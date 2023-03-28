package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.RatingMpa;
import ru.yandex.practicum.filmorate.storage.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.storage.RatingMpaStorage;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingMpaDaoTest {

    @Autowired
    private final RatingMpaStorage ratingMpaStorage;

    @Test
    public void testGetGenreByIdExistId(){
        RatingMpa ratingMpa = ratingMpaStorage.getRatingMpaById(1);
        Assertions.assertEquals(ratingMpa.getId(),1);
        Assertions.assertEquals(ratingMpa.getName(), "Комедия");
    }

    @Test
    public void testGetGenreByIdNoExistId(){
        RatingMpa ratingMpa = ratingMpaStorage.getRatingMpaById(100);
        Assertions.assertNull(ratingMpa);
        Assertions.assertEquals( "G", ratingMpa.getName());
        Assertions.assertEquals( "у фильма нет возрастных ограничений", ratingMpa.getDescription());
    }
    
    @Test
    public void testFindALL(){
        List<RatingMpa> all = ratingMpaStorage.findAll();
        Assertions.assertEquals(6, all.size());
    }

}

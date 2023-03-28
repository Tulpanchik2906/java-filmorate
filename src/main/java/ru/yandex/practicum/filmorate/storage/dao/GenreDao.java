package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storage.storage.GenreStorage;

import java.util.List;

@Component
public class GenreDao implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(int id) {
        List<Genre> genres =  jdbcTemplate.query("SELECT id, name FROM GENRE " +
                        "WHERE ID = ?",
                new BeanPropertyRowMapper<Genre>(Genre.class), id);
        if (genres.isEmpty()){
            return null;
        }
        return genres.get(0);
    }

    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM GENRE",
                new BeanPropertyRowMapper<Genre>(Genre.class));
    }

}

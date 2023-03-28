package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.RatingMpa;
import ru.yandex.practicum.filmorate.storage.storage.RatingMpaStorage;

import java.util.List;

@Component
public class RatingMpaDao implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingMpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RatingMpa getRatingMpaById(int id) {
        List<RatingMpa> ratingMpa = jdbcTemplate.query("SELECT * FROM RATING_MPA " +
                "WHERE ID = ?", new BeanPropertyRowMapper<RatingMpa>(RatingMpa.class), id);
        if (ratingMpa.isEmpty()) {
            return null;
        }
        return ratingMpa.get(0);
    }

    public List<RatingMpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM RATING_MPA",
                new BeanPropertyRowMapper<RatingMpa>(RatingMpa.class));
    }
}

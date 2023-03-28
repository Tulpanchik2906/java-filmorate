package ru.yandex.practicum.filmorate.storage.storage;

import ru.yandex.practicum.filmorate.models.RatingMpa;

import java.util.List;

public interface RatingMpaStorage {
    public RatingMpa getRatingMpaById(int id);

    public List<RatingMpa> findAll();
}

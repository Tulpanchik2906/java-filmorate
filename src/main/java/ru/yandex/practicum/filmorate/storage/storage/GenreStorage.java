package ru.yandex.practicum.filmorate.storage.storage;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

public interface GenreStorage {
    public Genre getGenreById(int id);

    public List<Genre> findAll();

}

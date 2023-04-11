package ru.yandex.practicum.filmorate.storage.storage;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmStorage {

    public void add(Film film);

    public void update(Film film);

    public void delete(Film film);

    public List<Film> findAll();

    public Film getFilmById(int id);

    public void clear();

    public void addLike(int filmId, int userId);

    public void deleteLike(int filmId, int userId);

    public int getLastFilmId();

}

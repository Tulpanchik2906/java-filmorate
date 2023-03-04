package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public void add(Film film);

    public void update(Film film);

    public void delete(Film film);

    public List<Film> findAll();

    public Map<Integer, Film> getFilms();

    public void clear();

}

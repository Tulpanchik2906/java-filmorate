package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void add(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void delete(Film film) {
        films.remove(film.getId());
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public void clear() {
        films.clear();
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.addLike(userId);
        films.put(film.getId(), film);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.deleteLike(userId);
        films.put(film.getId(), film);
    }

    @Override
    public int getLastFilmId() {
        List<Integer> list = films.keySet().stream()
                .collect(Collectors.toList());
        Collections.sort(list, Comparator.naturalOrder());
        return list.get(list.size() - 1);
    }

}

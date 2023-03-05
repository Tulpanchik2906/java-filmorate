package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{

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
    public List<Film> findAll(){
       return new ArrayList<>(films.values());
    }

    public Map<Integer, Film> getFilms(){
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public void clear() {
        films.clear();
    }
}

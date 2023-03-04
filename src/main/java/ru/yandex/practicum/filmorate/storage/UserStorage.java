package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    public void add(User user);

    public void update(User user);

    public void delete(User user);

    public List<User> findAll();

    public Map<Integer, User> getUsers();

    public void clear();
}

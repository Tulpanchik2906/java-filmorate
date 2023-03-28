package ru.yandex.practicum.filmorate.storage.storage;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {
    public void add(User user);

    public void update(User user);

    public void addFriend(int initiatorId, int acceptorId);

    public void deleteFriend(int initiatorId, int acceptorId);

    public void delete(User user);

    public List<User> findAll();

    public User getUserById(int id);

    public void clear();

    public int getLastUserId();
}

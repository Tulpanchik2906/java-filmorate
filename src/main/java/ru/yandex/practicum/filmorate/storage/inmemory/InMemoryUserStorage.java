package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void add(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void addFriend(int initiatorId, int acceptorId) {
        User user = users.get(initiatorId);
        user.addFriend(acceptorId);
        users.put(initiatorId, user);

        User friend = users.get(acceptorId);
        friend.addFriend(initiatorId);
        users.put(acceptorId, friend);
    }

    @Override
    public void deleteFriend(int initiatorId, int acceptorId) {
        User user = users.get(initiatorId);
        user.deleteFriend(acceptorId);
        users.put(initiatorId, user);

        User friend = users.get(acceptorId);
        friend.deleteFriend(initiatorId);
        users.put(acceptorId, friend);

    }


    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }


    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public int getLastUserId() {
        List<Integer> list = users.keySet().stream()
                .collect(Collectors.toList());
        Collections.sort(list, Comparator.naturalOrder());
        return list.get(list.size() - 1);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    private int generatedId = 0;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public List<User> getUsers() {
        return userStorage.findAll();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        int id = generateId();
        user.setId(id);
        user.setName(getUserName(user));
        userStorage.add(user);
        return user;
    }

    public User updateUser(User user) {
        checkExistUserForUpdateUser(user);
        user.setName(getUserName(user));
        userStorage.update(user);
        return user;
    }

    public List<User> getFriendsByUserId(int userId) {
        User user = getUserById(userId);
        return user.getFriendsIds().stream()
                .map(friendId -> userStorage.getUserById(friendId))
                .collect(Collectors.toList());

    }

    public List<User> getCommonFriends(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        return user.getFriendsIds().stream()
                .map(id -> userStorage.getUserById(id))
                .filter(id -> friend.getFriendsIds().contains(id))
                .collect(Collectors.toList());
    }

    public void putFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
    }

    public void clearUsers() {
        userStorage.clear();
        generatedId = 0;
    }

    private void checkExistUserForUpdateUser(User user) {
        if (!userStorage.getUsers().containsKey(user.getId())) {
            log.error("Пользователь с id " + user.getId() + " не существует.");
            throw new NotFoundException("Пользователь с id " + user.getId() + " не существует.");
        }
    }

    private String getUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin();
        }
        return user.getName();
    }

    private int generateId() {
        generatedId++;
        return generatedId;
    }
}

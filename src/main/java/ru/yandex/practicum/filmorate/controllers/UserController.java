package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private static int generateId = 0;

    @GetMapping
    public List<User> getUsers(){
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user){
        checkRepeatUserForCreateUser(user);
        generateId++;
        user.setId(generateId);
        user.setName(getUserName(user));
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user){
        checkExistUserForUpdateUser(user);
        user.setName(getUserName(user));
        users.put(user.getId(), user);
        return user;
    }

    private void checkRepeatUserForCreateUser(User user){
        if(users.containsKey(user.getId())){
            log.error("Пользователь с id " + user.getId()+" уже существует.");
            throw new ValidationException("Пользователь с id " + user.getId()+" уже существует.");
        }
    }

    private void checkExistUserForUpdateUser(User user){
        if(!users.containsKey(user.getId())){
            log.error("Пользователь с id " + user.getId()+" не существует.");
            throw new ValidationException("Пользователь с id " + user.getId()+" не существует.");
        }
    }

    private String getUserName(User user){
        if(user.getName() == null || user.getName().isBlank()){
            return user.getLogin();
        }
        return user.getName();
    }
}


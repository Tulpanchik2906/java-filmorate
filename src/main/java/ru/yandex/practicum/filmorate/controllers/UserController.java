package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
    private final Map<Integer, User> users = new HashMap<>();
    private int generateId = 0;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        generateId++;
        user.setId(generateId);
        user.setName(getUserName(user));
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        checkExistUserForUpdateUser(user);
        user.setName(getUserName(user));
        users.put(user.getId(), user);
        return user;
    }

    public void clearFilms() {
        users.clear();
        generateId = 0;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception)
            throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(exception));
    }

    private void checkExistUserForUpdateUser(User user) {
        if (!users.containsKey(user.getId())) {
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
}


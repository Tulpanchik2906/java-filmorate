package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {

    private int id;

    @Email(message = "Строка не является Email.")
    private String email;

    @NotNull(message = "Логин не может быть null. Необходимо ввести логин.")
    @NotEmpty(message = "Логин не может быть пустым.")
    @NotBlank(message = "Логин не может состоять из одних пробелов.")
    private String login;

    private String name;

    @Past(message = "День рождение должно быть в прошлом.")
    private LocalDate birthday;

    private Set<Integer> friendsIds = new TreeSet<>();

    public void addFriend(int friendId){
        friendsIds.add(friendId);
    }

    public void deleteFriend(int friendId){
        friendsIds.remove(friendId);
    }
}
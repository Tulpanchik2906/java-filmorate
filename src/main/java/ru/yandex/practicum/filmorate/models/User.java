package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private int id;

    @Email(message = "Строка не является Email")
    private String email;

    @NotNull(message = "Необходимо ввести логин")
    @NotEmpty(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может состоять из одних пробелов")
    private String login;

    private String name;

    @Past(message = "День рождение должно быть в прошлом")
    private LocalDate birthday;
}
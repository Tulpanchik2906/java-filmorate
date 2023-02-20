package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @Email
    private String email;

    @NotNull
    @NotBlank
    @NotEmpty
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotNull(message = "Необходимо указать название фильма")
    @NotEmpty(message = "Название фильма не может быть пустым")
    @NotBlank(message = "Название фильма не может быть состоять только из пробелов")
    private String name;

    @Size(max = 200, message = "Описанием должно быть не более 200 символов" )
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Длительность фильма должна быть больше 0 минут")
    private int duration;
}

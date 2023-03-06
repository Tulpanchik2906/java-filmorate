package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    private int id;
    @NotNull(message = "Название фильма не может быть null. Необходимо указать название фильма")
    @NotEmpty(message = "Название фильма не может быть пустым")
    @NotBlank(message = "Название фильма не может быть состоять только из пробелов")
    private String name;

    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Min(value = 1, message = "Длительность фильма должна быть больше 0 минут")
    private int duration;

    private Set<Integer> likeUserIds = new TreeSet<>();

    public void addLike(int userId){
        likeUserIds.add(userId);
    }

    public void deleteLike(int userId){
        likeUserIds.remove(userId);
    }

}

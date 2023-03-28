package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class RatingMpa {
    private int id;
    private String name;
    private String description;
}

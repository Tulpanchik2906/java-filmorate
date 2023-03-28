package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class Genre implements Comparable<Genre> {

    private Integer id;
    private String name;

    @Override
    public int compareTo(Genre o) {
        return this.id - o.id;
    }
}

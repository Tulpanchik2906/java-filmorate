package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.RatingMpa;
import ru.yandex.practicum.filmorate.storage.storage.RatingMpaStorage;

import java.util.List;

@Service
public class RatingMpaService {
    private final RatingMpaStorage ratingMpaStorage;

    @Autowired
    public RatingMpaService(RatingMpaStorage ratingMpaStorage) {
        this.ratingMpaStorage = ratingMpaStorage;
    }

    public RatingMpa getRatingMpaById(int id) {
        RatingMpa ratingMpa = ratingMpaStorage.getRatingMpaById(id);
        if(ratingMpa == null){
            throw new NotFoundException("rating_mpa c id " + id +" не найден");
        }
        return ratingMpa;
    }

    public List<RatingMpa> getRatingMpaList() {
        return ratingMpaStorage.findAll();
    }
}

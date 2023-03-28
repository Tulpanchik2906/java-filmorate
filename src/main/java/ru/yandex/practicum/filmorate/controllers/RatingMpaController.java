package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingMpaController {

    private final RatingMpaService ratingMpaService;

    public RatingMpaController(RatingMpaService ratingMpaService) {
        this.ratingMpaService = ratingMpaService;
    }

    @GetMapping
    public List<RatingMpa> getRatingMpaList() {
        return ratingMpaService.getRatingMpaList();
    }

    @GetMapping("/{id}")
    public RatingMpa getRatingMpaById(@PathVariable Integer id) {
        return ratingMpaService.getRatingMpaById(id);
    }
}

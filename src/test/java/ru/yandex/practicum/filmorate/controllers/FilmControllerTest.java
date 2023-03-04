package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.models.Film;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmController filmController;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    public void beforeEach() {
        filmController.clearFilms();
    }

    @SneakyThrows
    @Test
    public void testEmptyListFilm() {
        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals("[]",
                        result.getResponse().getContentAsString()));

    }

    @SneakyThrows
    @Test
    public void testNoEmptyListFilm() {
        Film film = getAllFieldsFilm();
        mockMvc.perform(post("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String jsonFilmList = "[{\"id\":1,\"name\":\"Титаник\"," +
                "\"description\":\"Фильм о любви и затонувшем корабле.\"" +
                ",\"releaseDate\":\"1997-12-19\",\"duration\":194}]";
        mockMvc.perform(get("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(jsonFilmList,
                        result.getResponse().getContentAsString(StandardCharsets.UTF_8)));

    }

    @SneakyThrows
    @Test
    public void testOkCreateFilm() {
        Film film = getAllFieldsFilm();
        Film filmExp = getAllFieldsFilm();
        mockMvc.perform(post("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name")
                        .value(filmExp.getName()))
                .andExpect(jsonPath("$.description")
                        .value(filmExp.getDescription()))
                .andExpect(jsonPath("$.releaseDate")
                        .value(filmExp.getReleaseDate().format(dateFormatter)))
                .andExpect(jsonPath("$.duration")
                        .value(filmExp.getDuration()));


    }

    @SneakyThrows
    @Test
    public void testOkUpdateFilm() {
        Film film = getAllFieldsFilm();
        Film filmExp = getAllFieldsFilm();
        filmExp.setName("Посмотреть на Лео");

        mockMvc.perform(post("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setName("Посмотреть на Лео");

        mockMvc.perform(put("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name")
                        .value(filmExp.getName()))
                .andExpect(jsonPath("$.description")
                        .value(filmExp.getDescription()))
                .andExpect(jsonPath("$.releaseDate")
                        .value(filmExp.getReleaseDate().format(dateFormatter)))
                .andExpect(jsonPath("$.duration")
                        .value(filmExp.getDuration()));

    }

    @SneakyThrows
    @Test
    public void testErrorEmptyFilm() {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    public void testErrorWithNullNameFilm() {
        Film film = getAllFieldsFilm();
        film.setName(null);
        checkPostAnnotationValidation(film, "Название фильма не может быть null." +
                " Необходимо указать название фильма");
    }

    @SneakyThrows
    @Test
    public void testErrorWithEmptyNameFilm() {
        Film film = getAllFieldsFilm();
        film.setName("");
        checkPostAnnotationValidation(film, "Название фильма " +
                "не может быть пустым");
    }

    @SneakyThrows
    @Test
    public void testErrorWithSpacesNameFilm() {
        Film film = getAllFieldsFilm();
        film.setName("   ");
        checkPostAnnotationValidation(film, "Название фильма не может " +
                "быть состоять только из пробелов");
    }

    @SneakyThrows
    @Test
    public void testErrorWithMore200SymbolFilm() {
        Film film = getAllFieldsFilm();
        String descriptionStr = "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова," +
                " который задолжал им деньги, а именно 20 миллионов. " +
                "о Куглов, который за время «своего отсутствия», " +
                "стал кандидатом Коломбани.";
        film.setDescription(descriptionStr);
        checkPostAnnotationValidation(film, "Описание должно быть не более 200 символов");
    }

    @SneakyThrows
    @Test
    public void testErrorWith0DurationFilm() {
        Film film = getAllFieldsFilm();
        film.setDuration(0);
        checkPostAnnotationValidation(film, "Длительность фильма " +
                "должна быть больше 0 минут");
    }

    @SneakyThrows
    @Test
    public void testErrorWithOldDateFilm() {
        Film film = getAllFieldsFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        checkPostAnnotationValidation(film, "Кино тогда еще не родилось.");
    }

    @SneakyThrows
    @Test
    public void testErrorUpdateNewFilm() {
        Film film = getAllFieldsFilm();
        film.setId(1000);
        mockMvc.perform(put("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(h -> Assertions.assertTrue(
                        h.getResolvedException().getMessage()
                                .contains("Фильм с id " + film.getId() + " не существует.")))
                .andExpect(status().isNotFound());
    }

    private Film getAllFieldsFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("Титаник");
        film.setDescription("Фильм о любви и затонувшем корабле.");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(194);
        return film;
    }

    private String getJsonFilm(Film film) throws JsonProcessingException {
        return objectMapper.writeValueAsString(film);
    }

    private void checkPostAnnotationValidation(Film film, String errorText)
            throws Exception {
        mockMvc.perform(post("/films")
                        .content(getJsonFilm(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(h -> Assertions.assertTrue(
                        h.getResolvedException().getMessage()
                                .contains(errorText))
                )
                .andExpect(status().is4xxClientError());
    }
}

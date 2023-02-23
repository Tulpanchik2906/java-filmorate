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
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController.clearFilms();
    }

    @SneakyThrows
    @Test
    public void testErrorPostEmptyFilm() {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    public void testErrorPutEmptyFilm() {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetErrorNoValidEmail() throws Exception {
        User user = getAllFieldsUser();
        user.setEmail("mail.ru");
        checkPostAnnotationValidation(user, "Строка не является Email.");
    }

    @Test
    public void testGetErrorNullLogin() throws Exception {
        User user = getAllFieldsUser();
        user.setLogin(null);
        checkPostAnnotationValidation(user, "Логин не может быть null. Необходимо ввести логин.");
    }

    @Test
    public void testGetErrorEmptyLogin() throws Exception {
        User user = getAllFieldsUser();
        user.setLogin(null);
        checkPostAnnotationValidation(user, "Логин не может быть пустым.");
    }

    @Test
    public void testGetErrorSpaceLogin() throws Exception {
        User user = getAllFieldsUser();
        user.setLogin("    ");
        checkPostAnnotationValidation(user, "Логин не может состоять из одних пробелов.");
    }

    @Test
    public void testGetErrorFeatureBirthday() throws Exception {
        User user = getAllFieldsUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        checkPostAnnotationValidation(user, "День рождение должно быть в прошлом.");
    }

    @Test
    public void testGetErrorUpdateNewUser() throws Exception {
        User user = getAllFieldsUser();
        user.setId(1001);
        mockMvc.perform(put("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(h -> Assertions.assertTrue(
                        h.getResolvedException().getMessage()
                                .contains("Пользователь с id "
                                        + user.getId() + " не существует."))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEmptyUserList() throws Exception {
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals("[]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void testGetNoEmptyUserList() throws Exception {
        User user = getAllFieldsUser();
        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals("[{\"id\":1,\"email\":\"user@yandex.ru\"," +
                                "\"login\":\"Login user\"," +
                                "\"name\":\"Name user\",\"birthday\":\"1998-10-15\"}]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = getAllFieldsUser();
        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Name user"))
                .andExpect(jsonPath("$.login")
                        .value("Login user"))
                .andExpect(jsonPath("$.email")
                        .value("user@yandex.ru"))
                .andExpect(jsonPath("$.birthday")
                        .value("1998-10-15"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = getAllFieldsUser();
        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        user.setName("Update Name");
        mockMvc.perform(put("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Update Name"))
                .andExpect(jsonPath("$.login")
                        .value("Login user"))
                .andExpect(jsonPath("$.email")
                        .value("user@yandex.ru"))
                .andExpect(jsonPath("$.birthday")
                        .value("1998-10-15"));
    }

    @SneakyThrows
    @Test
    public void testCreateWithOutName() {
        User user = getAllFieldsUser();
        user.setName(null);
        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value(user.getLogin()));

    }

    @SneakyThrows
    @Test
    public void testUpdateWithOutName() {
        User user = getAllFieldsUser();
        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user.setName(null);

        mockMvc.perform(put("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value(user.getLogin()));

    }

    private User getAllFieldsUser() {
        User user = new User();
        user.setId(1);
        user.setName("Name user");
        user.setLogin("Login user");
        user.setEmail("user@yandex.ru");
        user.setBirthday(LocalDate.of(1998, 10, 15));

        return user;
    }

    private String getJsonUser(User user) throws JsonProcessingException {
        return objectMapper.writeValueAsString(user);
    }

    private void checkPostAnnotationValidation(User user, String errorText)
            throws Exception {
        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(h -> Assertions.assertTrue(
                        h.getResolvedException().getMessage()
                                .contains(errorText))
                )
                .andExpect(status().is4xxClientError());
    }
}

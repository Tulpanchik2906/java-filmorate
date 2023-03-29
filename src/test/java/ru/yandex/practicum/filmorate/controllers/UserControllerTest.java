package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    public void afterEach() {
        userController.clearUsers();
    }

    @SneakyThrows
    @Test
    public void testErrorPostEmptyUser() {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    public void testErrorPutEmptyUser() {
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

        String jsonExp = "[{\"id\":1,\"email\":\"user@yandex.ru\",\"login\":\"Login user\"," +
                "\"name\":\"Name user\",\"birthday\":\"1998-10-15\",\"friendsIds\":[]}]";

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(jsonExp,
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = getAllFieldsUser();
        User userExp = getAllFieldsUser();

        mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value(userExp.getName()))
                .andExpect(jsonPath("$.login")
                        .value(userExp.getLogin()))
                .andExpect(jsonPath("$.email")
                        .value(userExp.getEmail()))
                .andExpect(jsonPath("$.birthday")
                        .value(userExp.getBirthday().format(dateFormatter)));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = getAllFieldsUser();
        User userExp = getAllFieldsUser();
        userExp.setName("Update Name");

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
                        .value(userExp.getName()))
                .andExpect(jsonPath("$.login")
                        .value(userExp.getLogin()))
                .andExpect(jsonPath("$.email")
                        .value(userExp.getEmail()))
                .andExpect(jsonPath("$.birthday")
                        .value(userExp.getBirthday().format(dateFormatter)));
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

    @SneakyThrows
    @Test
    public void testAddFriend() {
        User user = getAllFieldsUser();
        int userId = addUser(user);
        int friendId = addUser(user);

        addFriend(userId, friendId);

        // проверяем, что друзья добавились первому пользователю
        String path = "/users/" + userId;
        mockMvc.perform(get(path)
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsIds")
                        .value(2));
    }

    @SneakyThrows
    @Test
    public void testDeleteFriend() {
        User user = getAllFieldsUser();
        int userId = addUser(user);
        int friendId = addUser(user);

        String path = "/users/" + userId + "/friends/" + friendId;

        mockMvc.perform(delete(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // проверяем, что друзья добавились первому пользователю
        path = "/users/" + userId;
        mockMvc.perform(get(path)
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsIds")
                        .isEmpty());
    }

    @SneakyThrows
    @Test
    @Ignore
    public void testGetListFriend() {
        User user = getAllFieldsUser();
        int userId = addUser(user);
        int friendId = addUser(user);

        addFriend(userId, friendId);

        String path = "/users/" + userId + "/friends/";

        String jsonExp = "[{\"id\":2,\"email\":\"user@yandex.ru\"," +
                "\"login\":\"Login user\",\"name\":\"Name user\"," +
                "\"birthday\":\"1998-10-15\",\"friendsIds\":[1]}]";

        mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(jsonExp,
                        result.getResponse().getContentAsString()));

        // Проверка, что и у friend появился user в друзьях
        path = "/users/" + friendId + "/friends/";

        String jsonExp2 = "[{\"id\":1,\"email\":\"user@yandex.ru\"," +
                "\"login\":\"Login user\",\"name\":\"Name user\"," +
                "\"birthday\":\"1998-10-15\",\"friendsIds\":[2]}]";

        mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(jsonExp2,
                        result.getResponse().getContentAsString()));
    }

    @SneakyThrows
    @Test
    @Ignore
    public void testGetCommonListFriend() {
        User user = getAllFieldsUser();
        int userId = addUser(user);
        int friendId = addUser(user);
        int friendId2 = addUser(user);

        addFriend(userId, friendId);
        addFriend(userId, friendId2);

        String path = "/users/" + friendId + "/friends/common/" + friendId2;

        String jsonExp = "[{\"id\":1,\"email\":\"user@yandex.ru\"," +
                "\"login\":\"Login user\"," +
                "\"name\":\"Name user\",\"birthday\":\"1998-10-15\"," +
                "\"friendsIds\":[2,3]}]";

        mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(jsonExp,
                        result.getResponse().getContentAsString()));
    }

    @SneakyThrows
    @Test
    public void testGetCommonEmptyListFriend() {
        User user = getAllFieldsUser();
        int userId = addUser(user);
        int friendId = addUser(user);
        int friendId2 = addUser(user);

        addFriend(userId, friendId);
        addFriend(userId, friendId2);

        String path = "/users/" + friendId + "/friends/common/" + userId;
        String jsonExp = "[]";

        mockMvc.perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertEquals(jsonExp,
                        result.getResponse().getContentAsString()));
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

    private int addUser(User user) throws Exception {
        MvcResult res = mockMvc.perform(post("/users")
                        .content(getJsonUser(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        User resUser = objectMapper
                .readValue(res.getResponse().getContentAsString(),
                        User.class);
        return resUser.getId();
    }

    private void addFriend(int userId, int friendId) throws Exception {
        String path = "/users/" + userId + "/friends/" + friendId;

        mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

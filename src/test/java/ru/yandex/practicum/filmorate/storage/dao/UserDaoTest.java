package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.storage.UserStorage;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserDaoTest {

    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    @AfterEach
    public void afterEach() {
        userStorage.clear();
    }

    @Test
    public void addUserTest() {
        User user = getAllFieldsUser();

        userStorage.add(user);

        User saveUser = userStorage.getUserById(user.getId());

        Assertions.assertNotNull(saveUser);
    }

    @Test
    public void updateUserTest() {
        User user = getAllFieldsUser();
        userStorage.add(user);

        user.setName("Измененное имя");
        userStorage.update(user);

        User saveUser = userStorage.getUserById(user.getId());

        Assertions.assertEquals("Измененное имя", saveUser.getName());
    }

    @Test
    public void addFriendOneLinkTest() {
        User user = getAllFieldsUser();
        User friend = getAllFieldsUser();
        friend.setId(user.getId() + 1);

        userStorage.add(user);
        userStorage.add(friend);

        userStorage.addFriend(user.getId(), friend.getId());

        User saveUser = userStorage.getUserById(user.getId());

        Assertions.assertEquals(1, saveUser.getFriendsIds().size());
        Assertions.assertTrue(saveUser.getFriendsIds().contains(friend.getId()));

    }

    @Test
    public void addFriendCrossLinkTest() {
        User user = getAllFieldsUser();
        User friend = getAllFieldsUser();
        friend.setId(user.getId() + 1);

        userStorage.add(user);
        userStorage.add(friend);

        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(friend.getId(), user.getId());

        User saveFriend = userStorage.getUserById(friend.getId());

        Assertions.assertEquals(1, saveFriend.getFriendsIds().size());
        Assertions.assertTrue(saveFriend.getFriendsIds().contains(user.getId()));

    }

    @Test
    public void deleteFriendOneLinkTest() {
        User user = getAllFieldsUser();
        User friend = getAllFieldsUser();
        friend.setId(user.getId() + 1);

        userStorage.add(user);
        userStorage.add(friend);

        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.deleteFriend(user.getId(), friend.getId());

        User saveUser = userStorage.getUserById(user.getId());

        Assertions.assertEquals(0, saveUser.getFriendsIds().size());
    }

    @Test
    public void deleteInitiatorFriendCrossLinkTest() {
        User user = getAllFieldsUser();
        User friend = getAllFieldsUser();
        friend.setId(user.getId() + 1);

        userStorage.add(user);
        userStorage.add(friend);

        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(friend.getId(), user.getId());

        userStorage.deleteFriend(user.getId(), friend.getId());

        User saveUser = userStorage.getUserById(user.getId());
        User saveFriend = userStorage.getUserById(friend.getId());


        Assertions.assertEquals(0, saveUser.getFriendsIds().size());
        Assertions.assertEquals(0, saveFriend.getFriendsIds().size());

    }

    @Test
    public void deleteAcceptorFriendCrossLinkTest() {
        User user = getAllFieldsUser();
        User friend = getAllFieldsUser();
        friend.setId(user.getId() + 1);

        userStorage.add(user);
        userStorage.add(friend);

        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(friend.getId(), user.getId());

        userStorage.deleteFriend(friend.getId(), user.getId());

        User saveUser = userStorage.getUserById(user.getId());
        User saveFriend = userStorage.getUserById(friend.getId());


        Assertions.assertEquals(0, saveUser.getFriendsIds().size());
        Assertions.assertEquals(0, saveFriend.getFriendsIds().size());

    }

    @Test
    public void deleteUserTest() {
        User user = getAllFieldsUser();
        userStorage.add(user);

        userStorage.delete(user);

        Assertions.assertTrue(userStorage.findAll().isEmpty());
    }

    @Test
    public void findAllTest() {
        User user = getAllFieldsUser();
        userStorage.add(user);

        user.setId(2);
        userStorage.add(user);

        Assertions.assertEquals(2, userStorage.findAll().size());
    }

    @Test
    public void getUserByExistId() {
        User user = getAllFieldsUser();
        userStorage.add(user);

        Assertions.assertNotNull(userStorage.getUserById(user.getId()));
    }

    @Test
    public void getUserByNoExistId() {
        Assertions.assertNull(userStorage.getUserById(1001));
    }

    @Test
    public void clearTest() {
        User user = getAllFieldsUser();
        userStorage.add(user);

        user.setId(user.getId() + 1);
        userStorage.add(user);

        userStorage.addFriend(user.getId(), user.getId() - 1);

        userStorage.clear();

        Assertions.assertEquals(0, userStorage.findAll().size());
    }

    @Test
    public void getLastUserIdTest() {
        User user = getAllFieldsUser();
        user.setId(1);
        userStorage.add(user);

        Assertions.assertEquals(1, userStorage.getLastUserId());

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

}

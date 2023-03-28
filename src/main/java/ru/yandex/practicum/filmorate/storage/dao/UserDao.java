package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;

@Component("UserDbStorage")
public class UserDao implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(User user) {
        jdbcTemplate.update(
                "INSERT INTO USERS (id, name, login, email, birthday) " +
                        "VALUES(?,?,?,?,?)",
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday());
        // Добавить друзей
        if (user.getFriendsIds() != null && !user.getFriendsIds().isEmpty()) {
            for (int friendId : user.getFriendsIds()) {
                addFriend(user.getId(), friendId);
            }
        }
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("UPDATE USERS SET name = ?, login = ?, email = ?, birthday = ? " +
                        "WHERE id = ?",
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update("DELETE FROM USERS WHERE ID = ?", user.getId());
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM USERS", (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM USERS WHERE ID = ?",
                (rs, rowNum) -> makeUser(rs), id);
        if (users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }

    @Override
    public void clear() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    @Override
    public int getLastUserId() {
        List<Integer> list = jdbcTemplate.queryForList("SELECT ID FROM USERS " +
                "ORDER BY ID DESC LIMIT 1", Integer.class);
        if (list.isEmpty()) {
            return -1;
        } else {
            return list.get(0);
        }
    }

    public void addFriend(int initiatorId, int acceptorId) {
        if (isFriend(initiatorId, acceptorId)) {
            jdbcTemplate.update(
                    "UPDATE FRIENDSHIP SET status = true" +
                            "WHERE initiator_id = ? AND acceptor_id = ? ",
                    acceptorId, initiatorId);
        } else {
            jdbcTemplate.update(
                    "INSERT INTO FRIENDSHIP values (?, ?, false)",
                    initiatorId,
                    acceptorId);
        }
    }

    @Override
    public void deleteFriend(int initiatorId, int acceptorId) {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP " +
                        "WHERE (initiator_id = ? AND acceptor_id = ?) OR " +
                        "(initiator_id = ? AND acceptor_id = ?)",
                initiatorId, acceptorId, acceptorId, initiatorId);
    }

    public List<Integer> getFriendsByUserId(int userId) {
        return jdbcTemplate.queryForList("SELECT ACCEPTOR_ID\n" +
                        "FROM FRIENDSHIP\n" +
                        "WHERE INITIATOR_ID = ?\n" +
                        "UNION\n" +
                        "SELECT INITIATOR_ID\n" +
                        "FROM FRIENDSHIP\n" +
                        "WHERE STATUS = TRUE AND ACCEPTOR_ID = ?", Integer.class,
                userId, userId);
    }

    // Проверка дружат ли пользователи
    private boolean isFriend(int initiatorId, int acceptorId) {
        List<Boolean> status = jdbcTemplate.query(
                "SELECT * FROM FRIENDSHIP\n" +
                        "WHERE INITIATOR_ID = ? AND ACCEPTOR_ID  = ? \n",
                (rs, rowNum) -> true, acceptorId, initiatorId);
        return status.size() == 1;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        // Добавить список друзей.
        user.setFriendsIds(new TreeSet<>(getFriendsByUserId(user.getId())));

        return user;
    }


}

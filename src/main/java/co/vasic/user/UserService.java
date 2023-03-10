package co.vasic.user;

import co.vasic.database.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements UserServiceInterface {
    private static UserService instance;

    private UserService() {
    }

    public static UserService getInstance() {
        if (UserService.instance == null) {
            UserService.instance = new UserService();
        }
        return UserService.instance;
    }

    @Override
    public UserInterface getUser(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, username, password, token, coins, status FROM users WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UserInterface user = User.builder()
                        .id(rs.getInt(1))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .token(rs.getString(4))
                        .coins(rs.getInt(5))
                        .status(rs.getString(6))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInterface getUserWithoutSensibleData(int id) {
        if (id == 0) {
            return null;
        }
        return ((User) this.getUser(id)).toBuilder().password(null).token(null).build();
    }

    @Override
    public UserInterface getUserByUsername(String username) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, username, password, token, coins, status FROM users WHERE username=?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UserInterface user = User.builder()
                        .id(rs.getInt(1))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .token(rs.getString(4))
                        .coins(rs.getInt(5))
                        .status(rs.getString(6))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInterface getUserByUsernameWithoutSensibleData(String username) {
        if (username == null) {
            return null;
        }
        return ((User) this.getUserByUsername(username)).toBuilder().password(null).token(null).build();
    }

    @Override
    public List<UserInterface> getUsers() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("SELECT id, username, password, token, coins, status FROM users;");

            List<UserInterface> users = new ArrayList<>();
            while (rs.next()) {
                users.add(User.builder()
                        .id(rs.getInt(1))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .token(rs.getString(4))
                        .coins(rs.getInt(5))
                        .status(rs.getString(6))
                        .build());
            }

            rs.close();
            sm.close();
            conn.close();

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserInterface addUser(UserInterface user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username, password, token, coins, status) VALUES(?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getToken());
            ps.setInt(4, user.getCoins());
            ps.setString(5, user.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getUser(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException ignored) {

        }
        return null;
    }

    @Override
    public UserInterface updateUser(int id, UserInterface user) {
        User oldUser = (User) this.getUser(id);
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET username = ?, password = ?, token = ?, coins = ?, status = ? WHERE id = ?;");

            ps.setString(1, user.getUsername() != null ? user.getUsername() : oldUser.getUsername());
            ps.setString(2, user.getPassword() != null ? user.getPassword() : oldUser.getPassword());
            ps.setString(3, user.getToken() != null ? user.getToken() : oldUser.getToken());
            ps.setInt(4, user.getCoins());
            ps.setString(5, user.getStatus() != null ? user.getStatus() : oldUser.getStatus());
            ps.setInt(6, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }

            return this.getUser(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteUser(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?;");
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return false;
            }

            ps.close();
            conn.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

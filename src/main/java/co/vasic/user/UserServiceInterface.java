package co.vasic.user;

import java.util.List;

public interface UserServiceInterface {
    UserInterface getUser(int id);

    UserInterface getUserWithoutSensibleData(int id);

    UserInterface getUserByUsername(String username);

    List<UserInterface> getUsers();

    UserInterface addUser(UserInterface user);

    UserInterface updateUser(int id, UserInterface user);

    boolean deleteUser(int id);
}

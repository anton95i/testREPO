package co.vasic.database;

import java.sql.Connection;

public interface DatabaseServiceInterface {
    Connection getConnection();
}

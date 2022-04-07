package dev.builditbear.db_interface;

import dev.builditbear.App;
import dev.builditbear.model.Customer;
import dev.builditbear.model.User;
import dev.builditbear.utility.Alerts;
import javafx.scene.control.Alert;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public final class ConnectionManager {
    private ConnectionManager() {
        throw new RuntimeException("Instantiation of ConnectionManager is not allowed.");
    }

    private static final String dbURL = "jdbc:mysql://localhost:3306/client_schedule";
    private static final String dbUsername = "sqlUser";
    private static final String dbPassword = "Passw0rd!";
    private static Connection connection = null;
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Establishes and saves a connection to the database described in dbURL.
     */
    public static void openConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        } catch(ClassNotFoundException e) {
            System.out.println(App.getBundle().getString("db_driver_not_found"));
            e.printStackTrace();
        } catch(SQLException e) {
            System.out.println(App.getBundle().getString("db_connection_failure"));
            e.printStackTrace();
        }
    }

    /**
     * Closes the current connection to the database described in dbURL variable.
     */
    public static void closeConnection() {
        try {
            connection.close();
        } catch(SQLException e) {
            System.out.println(App.getBundle().getString("db_connection_close_failure"));
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    /**
     * Validates the given username and password against the database records.
     */
    public static boolean authenticateLogin(String username, String password){
        ResourceBundle bundle = App.getBundle();

        PreparedStatement validateUser;
        PreparedStatement validatePassword;
        try {
            validateUser = connection.
                    prepareStatement("SELECT * FROM users WHERE User_Name = ?");
            validatePassword = connection.
                    prepareStatement("SELECT * FROM users WHERE Password = ?");
            validateUser.setString(1, username);
            validatePassword.setString(1, password);

            ResultSet userResult = validateUser.executeQuery();
            ResultSet passwordResult = validatePassword.executeQuery();
            if(!userResult.next()){
                Alerts.invalidUserAlert();
                return false;
            } else if(!passwordResult.next()) {
                Alerts.invalidPasswordAlert();
                return false;
            } else {
                // Successful login.
                currentUser = DbManager.getUser(username);
                return true;
            }
        } catch(SQLException e) {
            System.out.println(bundle.getString("db_sql_error"));
            return false;
        }
    }
}
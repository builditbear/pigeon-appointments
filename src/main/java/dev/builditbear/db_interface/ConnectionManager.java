package dev.builditbear.db_interface;

import javafx.scene.control.Alert;
import java.sql.*;

public class ConnectionManager {
    private static final String dbURL = "jdbc:mysql://localhost:3306/client_schedule";
    private static final String dbUsername = "sqlUser";
    private static final String dbPassword = "Passw0rd!";
    private static Connection connection = null;

    public static void openConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        } catch(ClassNotFoundException e) {
            System.out.println("The database driver was not found!");
            e.printStackTrace();
        } catch(SQLException e) {
            System.out.println("Failed to connect to db.");
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch(SQLException e) {
            System.out.println("Failed to close connection to db!");
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    /**
     * Validates the given username and password against the database records.
     */
    public static boolean authenticateLogin(String username, String password){
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
                invalidUserAlert();
                return false;
            } else if(!passwordResult.next()) {
                invalidPasswordAlert();
                return false;
            } else {
                return true;
            }
        } catch(SQLException e) {
            System.out.println("User authentication failed due to an SQL error while interacting with the database.");
            return false;
        }
    }

    private static void invalidUserAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Username");
        alert.setContentText("Username not found. Please enter a valid username.");
        alert.showAndWait();
    }

    private static void invalidPasswordAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Password");
        alert.setContentText("The entered password does not match the entered username. Please try again.");
        alert.showAndWait();
    }
}

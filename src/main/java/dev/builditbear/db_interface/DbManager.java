package dev.builditbear.db_interface;

import dev.builditbear.model.Country;
import dev.builditbear.model.Customer;
import dev.builditbear.model.FirstLevelDivision;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Handles the low level details of CRUD operations to and from the database, as well as user authentication protocols.
 */
public abstract class DbManager {
    private static String user = "test";

    /** Adds a new customer to the database. Note that because it is possible (though unlikely) for two people to exist
     * at the same address with the exact same name, this function will add a customer whether or not an apparently
     * identical person is already in the database.
     * @param name The full name of the person to be added.
     * @param address The street address of the person to be added.
     * @param postalCode The postal code of the address of the person to be added.
     * @param phone The phone numbers of the person to be added.
     * @param fldId The ID of the First Level Division of the address of the person to be added.
     * @return If the query successfully adds a customer based on the provided info, the integer 1 should be returned
     * since executeUpdate() returns the number of rows affected by the update, and we are only adding one customer.
     * Otherwise, 0 will be returned.
     */
    public static int addCustomer(String name, String address, String postalCode,
                                   String phone, int fldId) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement addUpdate;
        try {
            addUpdate = connection.prepareStatement("INSERT INTO customers VALUES (NULL, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?");
            // Set query parameters before executing.
            Object[] parameters = {name, address, postalCode, phone, user, user, fldId};
            for(int i = 1; i <= parameters.length; i++) {
                addUpdate.setObject(i, parameters[i]);
            }
            return addUpdate.executeUpdate();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    /**
     * Retrieves the customer data indicated by the provided customerId in the form of a new Customer object.
     * @param customerId The ID of the customer we wish to retrieve from the database.
     * @return A Customer object representing the data associated with the given customerId as it currently exists in
     * the database.
     */
    public static Customer getCustomer(int customerId) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM customers WHERE Customer_ID = ?");
            selectQuery.setString(1, Integer.toString(customerId));
            ResultSet queryResult = selectQuery.executeQuery();
            if(queryResult.next()) {
                String name = queryResult.getString(2);
                String address = queryResult.getString(3);
                String postalCode = queryResult.getString(4);
                String phone = queryResult.getString(5);
                LocalDateTime createDate = LocalDateTime.ofInstant(queryResult.getDate(6).toInstant(),
                                           ZoneId.systemDefault());
                String createdBy = queryResult.getString(7);
                Timestamp lastUpdate = queryResult.getTimestamp(8);
                String lastUpdatedBy = queryResult.getString(9);
                int divisionId = queryResult.getInt(10);
                return new Customer(customerId, name, address, postalCode, phone, createDate, createdBy,
                                    lastUpdate, lastUpdatedBy, divisionId);
            } else {
                return null;
            }
        } catch(SQLException ex) {
            // Should replace this with an alert.
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the First Level Division data associated with the given id from the Database.
     * @param fldId The id of the First Level Division in question.
     * @return An object representing the retrieved First Level Division.
     */
    public static FirstLevelDivision getFirstLevelDivision(int fldId) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM first_level_division WHERE Division_ID = ?");
            selectQuery.setString(1, Integer.toString(fldId));
            ResultSet queryResult = selectQuery.executeQuery();
            if(queryResult.next()) {
                String divisionName = queryResult.getString(2);
                LocalDateTime createDate = LocalDateTime.ofInstant(queryResult.getDate(3).toInstant(),
                                           ZoneId.systemDefault());
                String createdBy = queryResult.getString(4);
                Timestamp lastUpdate = queryResult.getTimestamp(5);
                String lastUpdatedBy = queryResult.getString(6);
                int countryId = queryResult.getInt(7);

                return new FirstLevelDivision(fldId, divisionName, createDate, createdBy,
                                              lastUpdate, lastUpdatedBy, countryId);
            } else {
                // No division exists with the given name.
                return null;
            }
        } catch(SQLException ex) {
            // Should replace this with an alert.
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the Country data corresponding to the given id from the database.
     * @param countryId The id of the country in question.
     * @return An object representing the country data retrieved from the database.
     */
    public static Country getCountry(int countryId) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM countries WHERE Country_ID = ?");
            selectQuery.setString(1, Integer.toString(countryId));
            ResultSet queryResult = selectQuery.executeQuery();
            if(queryResult.next()) {
                String countryName = queryResult.getString(2);
                LocalDateTime createDate = LocalDateTime.ofInstant(queryResult.getDate(3).toInstant(),
                                           ZoneId.systemDefault());
                String createdBy = queryResult.getString(4);
                Timestamp lastUpdate = queryResult.getTimestamp(5);
                String lastUpdateBy = queryResult.getString(6);
                return new Country(countryId, countryName, createDate, createdBy, lastUpdate, lastUpdateBy);
            } else {
                // No country exists with this name.
                return null;
            }
        } catch(SQLException ex) {
            // Should replace this with an alert.
            System.out.println(ex.getMessage());
            return null;
        }
    }
}

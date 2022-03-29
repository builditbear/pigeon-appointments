package dev.builditbear.db_interface;

import dev.builditbear.model.Country;
import dev.builditbear.model.Customer;
import dev.builditbear.model.FirstLevelDivision;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the low level details of CRUD operations to and from the database.
 */
public abstract class DbManager {

    /**
     * Determines whether or not the named FirstLevelDivision is in the named Country.
     * @param firstLevelDivisionName The name of the FirstLevelDivision in question.
     * @param countryName The name of the Country in question.
     * @return True if the FirstLevelDivision is in the given Country and False otherwise.
     */
    public static boolean isAssociatedWithCountry(String firstLevelDivisionName, String countryName) {
        FirstLevelDivision fld = getFirstLevelDivision(firstLevelDivisionName);
        Country fldCountry = getCountry(fld.getCountryId());
        if(fldCountry != null) {
            return fldCountry.getCountry().equals(countryName);
        } else {
            // This line is reached if no country matching the fld's countryId was found.
            return false;
        }
    }

    /**
     * Removes the customer associated with the provided customerId from the database.
     * @param customerId The ID of the customer we wish to remove from the database.
     * @return If the deletion is successful, the integer 1 is returned, since executeUpdate() returns the number of
     * rows affected by the operation. Similarly, if an exception is thrown or no customer with the given ID exists in
     * the database, 0 will be returned.
     */
    public static int removeCustomer(int customerId) {
        Connection connection = ConnectionManager.getConnection();

        PreparedStatement deleteUpdate;
        removeAssociatedAppointments(customerId);
        try {
            deleteUpdate = connection.prepareStatement("DELETE FROM customers WHERE Customer_ID = ?");
            deleteUpdate.setString(1, Integer.toString(customerId));
            return deleteUpdate.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method removeCustomer:");
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    /**
     * Removes any appointments associated with the given customerId. This is necessary prior to any customer record
     * deletion due to foreign key contraints within the database.
     * @param customerId The ID of the customer whose appointments are to be deleted from the database.
     * @return The number of appointments deleted by this operation.
     */
    public static int removeAssociatedAppointments(int customerId) {
        Connection connection = ConnectionManager.getConnection();

        PreparedStatement deleteUpdate;
        try {
            deleteUpdate = connection.prepareStatement("DELETE FROM appointments WHERE Customer_ID = ?");
            deleteUpdate.setString(1, Integer.toString(customerId));
            return deleteUpdate.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method removeAssociatedAppointments:");
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    /**
     * Update the customer record associated with the given ID, using the fields passed in as new values.
     * @param customerId The ID of the customer to be updated.
     * @param name The customer's new name.
     * @param address The customer's new address.
     * @param postalCode The customer's new postal code.
     * @param phone The customer's new phone number.
     * @param fldId The customer's new First Level Division.
     * @return The number of rows successfully updated by this operation. Should be 1 if successful, and 0 if an
     * exception was thrown or no customer matching the given ID exists in the database.
     */
    public static int updateCustomer(int customerId, String name, String address, String postalCode, String phone, int fldId) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement updateRecord;
        try {
            updateRecord = connection.prepareStatement(
                    "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?" +
                            ", Last_Update = NOW(), Last_Updated_By = ? WHERE Customer_ID = ?");
            Object[] parameters = {name, address, postalCode, phone, fldId, ConnectionManager.getCurrentUser()};
            for(int i = 1; i <= parameters.length; i++) {
                updateRecord.setObject(i, parameters[i - 1]);
            }
            updateRecord.setString(7, Integer.toString(customerId));
            return updateRecord.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method updateCustomer:");
            System.out.println(ex.getMessage());
            return 0;
        }
    }
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
            String user = ConnectionManager.getCurrentUser();
            Object[] parameters = {name, address, postalCode, phone, user, user, fldId};
            for(int i = 1; i <= parameters.length; i++) {
                addUpdate.setObject(i, parameters[i - 1]);
            }
            return addUpdate.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method addCustomer:");
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
                return createCustomer(queryResult);
            } else {
                return null;
            }
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getCustomer:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Returns a new Customer whose data reflects the record pointed to by the given ResultSet object.
     * @param queryResult A ResultSet pointing to a record we wish to create a new Customer object based on.
     * @return A new Customer object representing the record being pointed to by the ResultSet.
     * @throws SQLException In the event of an error encountered while communicating with the database.
     */
    private static Customer createCustomer(ResultSet queryResult) {
        try {
            int customerId = queryResult.getInt(1);
            String name = queryResult.getString(2);
            String address = queryResult.getString(3);
            String postalCode = queryResult.getString(4);
            String phone = queryResult.getString(5);
            LocalDateTime createDate = LocalDateTime.ofInstant(queryResult.getTimestamp(6).toInstant(),
                    ZoneId.systemDefault());
            String createdBy = queryResult.getString(7);
            Timestamp lastUpdate = queryResult.getTimestamp(8);
            String lastUpdatedBy = queryResult.getString(9);
            int divisionId = queryResult.getInt(10);
            return new Customer(customerId, name, address, postalCode, phone, createDate, createdBy,
                    lastUpdate, lastUpdatedBy, divisionId);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method createCustomer:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves an array representing all customer records currently in the database.
     * @return An array containing all customer records currently in the database. Will be empty if there are no rows,
     * and null in the event of an SQLException occurring.
     */
    public static ArrayList<Customer> getAllCustomers() {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM customers");
            ResultSet queryResult = selectQuery.executeQuery();
            ArrayList<Customer> customers = new ArrayList<>();
            boolean moreRows = queryResult.next();
            while(moreRows) {
                Customer c = createCustomer(queryResult);
                customers.add(c);
                moreRows = queryResult.next();
            }
            return customers;
        } catch(SQLException ex) {
            System.out.println("The following exception was encountered in method getAllCustomers:");
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
            selectQuery = connection.prepareStatement("SELECT * FROM first_level_divisions WHERE Division_ID = ?");
            selectQuery.setString(1, Integer.toString(fldId));
            ResultSet queryResult = selectQuery.executeQuery();
            return createFirstLevelDivision(queryResult);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getFirstLevelDivision:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Alternate lookup method for FirstLevelDivision records which takes the name associated with the
     * FirstLevelDivision instead.
     * @param name The name associated with the FirstLevelDivision in question.
     * @return A FirstLevelDivision object reflecting the data associated with the given name in the database.
     */
    public static FirstLevelDivision getFirstLevelDivision(String name) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM first_level_divisions WHERE Division = ?");
            selectQuery.setString(1, name);
            ResultSet queryResult = selectQuery.executeQuery();
            return createFirstLevelDivision(queryResult);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getFirstLevelDivision(name):");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Generates a new FirstLevelDivision reflecting the data in the ResultSet passed in.
     * @param queryResult  A ResultSet representing an SQL response containing a single FirstLevelDivision record.
     * @return A new FirstLevelDivision object based on the ResultSet passed in.
     * @throws SQLException Thrown in the event of a communication error with the SQL database.
     */
    private static FirstLevelDivision createFirstLevelDivision(ResultSet queryResult) throws SQLException{
        if(queryResult.next()) {
            int divisionId = queryResult.getInt(1);
            String divisionName = queryResult.getString(2);
            LocalDateTime createDate = LocalDateTime.ofInstant(queryResult.getTimestamp(3).toInstant(),
                    ZoneId.systemDefault());
            String createdBy = queryResult.getString(4);
            Timestamp lastUpdate = queryResult.getTimestamp(5);
            String lastUpdatedBy = queryResult.getString(6);
            int countryId = queryResult.getInt(7);

            return new FirstLevelDivision(divisionId, divisionName, createDate, createdBy,
                    lastUpdate, lastUpdatedBy, countryId);
        } else {
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
            return createCountry(queryResult);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getCountry(countryId):");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Alternate method for country lookup which takes the country's name (under the assumption that all country names
     * are unique) instead of an id.
     * @param name The unique name of the country we wish to retrieve from the database.
     * @return A Country object representing the country data retrieved from the database. Null if the country does not
     * exist in the database.
     */
    public static Country getCountry(String name) {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM countries WHERE Division = ?");
            selectQuery.setString(1, name);
            ResultSet queryResult = selectQuery.executeQuery();
            return createCountry(queryResult);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getCountry(name):");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Generates a Country object reflecting the information contained in the ResultSet passed in.
     * @param queryResult A ResultSet containing the results of an SQL query for a country record.
     * @return A new Country object representing the data in the ResultSet.
     * @throws SQLException Thrown in the event of any communication error with the database during interaction.
     */
    private static Country createCountry(ResultSet queryResult) throws SQLException{
        if(queryResult.next()) {
            int countryId = queryResult.getInt(1);
            String countryName = queryResult.getString(2);
            LocalDateTime createDate = LocalDateTime.ofInstant(queryResult.getTimestamp(3).toInstant(),
                    ZoneId.systemDefault());
            String createdBy = queryResult.getString(4);
            Timestamp lastUpdate = queryResult.getTimestamp(5);
            String lastUpdateBy = queryResult.getString(6);
            return new Country(countryId, countryName, createDate, createdBy, lastUpdate, lastUpdateBy);
        } else {
            return null;
        }
    }
}

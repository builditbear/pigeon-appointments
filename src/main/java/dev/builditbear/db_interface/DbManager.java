package dev.builditbear.db_interface;

import dev.builditbear.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Handles the low level details of CRUD operations to and from the database.
 */
public final class DbManager {
    private DbManager() {
        throw new RuntimeException("Instantiation of DbManager is not allowed.");
    }

    /**
     * Adds a new appointment record to the database.
     * @param title The appointment's title.
     * @param description A brief description of the appointment's purpose.
     * @param location A brief description of where the appointment is to take place.
     * @param type The kind of appointment taking place.
     * @param start A LocalDateTime object representing the appointment's starting time.
     * @param end A LocalDateTime object representing the appointment's end time.
     * @param customerName The name of the customer this appointment is being held with.
     * @param contactName The name of the contact to be associated with this appointment.
     * @return 1 if the add operation was successful, and 0 if an exception was thrown during interaction with SQL
     * database or input was improperly formatted.
     */
    public static int addAppointment(String title, String description, String location, String type,
                                     LocalDateTime start, LocalDateTime end, String customerName, String contactName) {
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement addRecord = connection.prepareStatement("INSERT INTO appointments VALUES " +
                    "(NULL, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)");
            User user = ConnectionManager.getCurrentUser();
            int customerId;
            int contactId;
            try {
                customerId = getCustomer(customerName).getId();
                contactId = getContact(contactName).getId();
            } catch(NullPointerException ex) {
                System.out.println("The customer or contact name given for this appointment does not match any records.");
                System.out.println("Check the names spelling and try again.");
                return -1;
            }
            Object[] parameters = {title, description, location, type, start, end, user.getName(), user.getName(),
                                   Integer.toString(customerId),
                                   Integer.toString(user.getId()),
                                   Integer.toString(contactId)};
            for(int i = 1; i <= parameters.length; i++) {
                addRecord.setObject(i, parameters[i - 1]);
            }
            return addRecord.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method addAppointment:");
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
            addUpdate = connection.prepareStatement("INSERT INTO customers VALUES (NULL, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?)");
            // Set query parameters before executing.
            User user = ConnectionManager.getCurrentUser();
            Object[] parameters = {name, address, postalCode, phone, user.getName(), user.getName(), fldId};
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
     * Retrieves a new Contact object representing the SQL record corresponding to the given contact name. The contact
     * name is assumed to be unique, as it will return the first matching record found otherwise.
     * @param contact The name of the contact in question.
     * @return A Contact object with data corresponding to the first SQL record matching the given name.
     */
    public static Contact getContact(String contact) {
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement contactQuery =
                    connection.prepareStatement("SELECT * FROM contacts WHERE Contact_Name = ?");
            contactQuery.setString(1, contact);
            ResultSet queryResult = contactQuery.executeQuery();
            return createContact(queryResult);
        } catch(SQLException ex) {
            System.out.println("SQLException occurred in method getContact:");
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

    public static Customer getCustomer(String customer) {
        try (ResultSet result = processQuery("SELECT * FROM customers WHERE Customer_Name = ?",
                new Object[] {customer})){
            return createCustomer(result);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getCustomer:");
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

    public static ArrayList<FirstLevelDivision> getAllFirstLevelDivisions() {
        try {
            ResultSet queryResult = processQuery("SELECT * FROM first_level_divisions");
            ArrayList<FirstLevelDivision> firstLevelDivisions = new ArrayList<>();
            boolean moreRows = queryResult.next();
            while(moreRows) {
                FirstLevelDivision fld = createFirstLevelDivision(queryResult);
                firstLevelDivisions.add(fld);
                moreRows = queryResult.next();
            }
            return firstLevelDivisions;
        } catch(SQLException ex) {
            System.out.println("SQLException occurred in method getAllFirstLevelDivisions:");
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
     * Retrieve the ID of the First Level Division corresponding to the name passed in.
     * @param firstLevelDivisionName The name of the First Level Division we want the ID for.
     * @return The ID of the First Level Division corresponding to the name passed in, or -1 if no division matching
     * that name exists in the database.
     */
    public static int getFldId(String firstLevelDivisionName) {
        FirstLevelDivision fld = getFirstLevelDivision(firstLevelDivisionName);
        if(fld != null) {
            return fld.getId();
        } else {
            return -1;
        }
    }

    /**
     * Creates a new Contact object based on the info contained in the given ResultSet.
     * @param queryResult A ResultSet containing the results of a Contact record query.
     * @return A new Contact object, or null if the ResultSet passed in was empty.
     * @throws SQLException Thrown in the event of an error while interacting with the SQL ResultSet.
     */
    private static Contact createContact(ResultSet queryResult) throws SQLException{
        if(queryResult.next()) {
            int id = queryResult.getInt(1);
            String contactName = queryResult.getString(2);
            String email = queryResult.getString(3);
            return new Contact(id, contactName, email);
        }
        return null;
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

    /**
     * Returns a new Customer whose data reflects the record pointed to by the given ResultSet object.
     * @param queryResult A ResultSet pointing to a record we wish to create a new Customer object based on.
     * @return A new Customer object representing the record being pointed to by the ResultSet.
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
        } catch(NullPointerException ex) {
            System.out.println("A NullPointerException was encountered in method createCustomer:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Generates a new FirstLevelDivision reflecting the data in the ResultSet passed in.
     * @param queryResult  A ResultSet representing an SQL response containing a single FirstLevelDivision record.
     * @return A new FirstLevelDivision object based on the ResultSet passed in.
     */
    private static FirstLevelDivision createFirstLevelDivision(ResultSet queryResult) {
        try {
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
        } catch(SQLException ex){
            System.out.println("SQLException encountered in method createFirstLevelDivision:");
            System.out.println(ex.getMessage());
            return null;
        } catch(NullPointerException ex) {
            System.out.println("NullPointerException encountered in method createFirstLevelDivision:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Determines whether or not the named FirstLevelDivision is in the named Country.
     * @param fld The FirstLevelDivision in question.
     * @param countryName The name of the Country in question.
     * @return True if the FirstLevelDivision is in the given Country and False otherwise.
     */
    public static boolean isAssociatedWithCountry(FirstLevelDivision fld, String countryName) {
        try {
            // Compare the country name given to the country of the First Level Division passed in by name, returning
            // the result.
            return Objects.requireNonNull(getCountry(fld.getCountryId())).getName().equals(countryName);
        } catch(NullPointerException ex) {
            System.out.println("NullPointerException encountered in method isAssociatedWithCountry:");
            System.out.println(ex.getMessage());
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
     * deletion due to foreign key constraints within the database.
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
     * @param fldId The customer's new First Level Division ID.
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

    /**
     * Sets up and executes an SQL prepared statement with the given query parameters, returning the result.
     * @param queryString The SQL statement to be executed, with the character '?' denoting a parameter.
     * @param parameters An object array containing parameter values, possibly of different types, in order of appearance
     *                   in the SQL statement to be executed. Note that incoming parameters must already be of the type
     *                   intended to be submitted to the SQL server.
     * @return A ResultSet containing the outcome of the statement's execution.
     */
    private static ResultSet processQuery(String queryString, Object[] parameters) throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement query = connection.prepareStatement(queryString);
        for(int i = 1; i <= parameters.length; i++) {
            query.setObject(i, parameters[i - 1]);
        }
        return query.executeQuery();
    }

    /**
     * Sets up and executes an SQL prepared statement corresponding to the string passed in and returns the result.
     * @param queryString The SQL statement to be executed.
     * @return A ResultSet containing the server response.
     * @throws SQLException Thrown in the event of a communication error with the SQL server.
     */
    private static ResultSet processQuery(String queryString) throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement query = connection.prepareStatement(queryString);
        return query.executeQuery();
    }
}

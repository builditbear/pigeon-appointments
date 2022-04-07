package dev.builditbear.db_interface;

import dev.builditbear.controller.AppointmentsController;
import dev.builditbear.model.*;
import dev.builditbear.utility.TimeConversion;

import java.sql.*;
import java.time.LocalDate;
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
     * Adds a new appointment record to the database. Note that it is the responsibility of any method calling this method
     * to ensure that the customerId and contactId passed to this method are valid, as an SQLException due to a foreign
     * key error will be thrown otherwise.
     * @param title The appointment's title.
     * @param description A brief description of the appointment's purpose.
     * @param location A brief description of where the appointment is to take place.
     * @param type The kind of appointment taking place.
     * @param start A LocalDateTime object representing the appointment's starting time.
     * @param end A LocalDateTime object representing the appointment's end time.
     * @param customerId The id of the customer this appointment is being held with.
     * @param contactId The id of the contact to be associated with this appointment.
     * @return 1 if the add operation was successful, and 0 if an exception was thrown during interaction with SQL
     * database or input was improperly formatted.
     */
    public static int addAppointment(String title, String description, String location, String type,
                                     LocalDateTime start, LocalDateTime end, int customerId, int userId, int contactId) {
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement addRecord = connection.prepareStatement("INSERT INTO appointments VALUES " +
                    "(NULL, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)");
            User appointmentCreator = ConnectionManager.getCurrentUser();
            Object[] parameters = {title, description, location, type, Timestamp.valueOf(start), Timestamp.valueOf(end),
                    appointmentCreator.getName(), appointmentCreator.getName(), customerId, userId, contactId};
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
     * Retrieves a new Contact object matching the given ID from the SQL database.
     * @param contactId The ID of the Contact record we want as an object.
     * @return A new Contact object matching the given ID, or null if an error is encountered or no Contact record with
     * that ID exists in the database.
     */
    public static Contact getContact(int contactId) {
        Object[] parameters = {contactId};
        try(ResultSet result = processQuery("SELECT * FROM contacts WHERE Contact_ID = ?", parameters)) {
            if(result.next()) {
                return createContact(result);
            } else {
                return null;
            }
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method getContact:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try(ResultSet result = processQuery("SELECT * FROM contacts")) {
            boolean moreRows = result.next();
            while(moreRows) {
                contacts.add(createContact(result));
                moreRows = result.next();
            }
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method getAllContacts.");
            System.out.println(ex.getMessage());
        }
        return contacts;
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
     * Retrieves an array representing all customer records currently in the database.
     * @return An array containing all customer records currently in the database. Will be empty if there are no rows,
     * and null in the event of an SQLException occurring.
     */
    public static ArrayList<Customer> getAllCustomers() {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement selectQuery;
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            selectQuery = connection.prepareStatement("SELECT * FROM customers");
            ResultSet queryResult = selectQuery.executeQuery();
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
            return customers;
        }
    }

    /**
     * Retrieves an ArrayList of all Appointment objects on file within the SQL database.
     * @return An ArrayList of Appointment objects representing every appointment record in the SQL database, or null
     * if a communication error is encountered.
     */
    public static ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        try(ResultSet results = processQuery("SELECT * FROM appointments")) {
            boolean moreRows = results.next();
            while(moreRows) {
                Appointment a = createAppointment(results);
                appointments.add(a);
                moreRows = results.next();
            }
            return appointments;
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method getAllAppointments:");
            System.out.println(ex.getMessage());
            return appointments;
        }
    }

    public static ArrayList<FirstLevelDivision> getAllFirstLevelDivisions() {
        ArrayList<FirstLevelDivision> firstLevelDivisions = new ArrayList<>();
        try {
            ResultSet queryResult = processQuery("SELECT * FROM first_level_divisions");
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
            return firstLevelDivisions;
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
            if(queryResult.next()) {
                return createFirstLevelDivision(queryResult);
            } else {
                throw new NullPointerException("The query returned no results.");
            }
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method getFirstLevelDivision:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static User getUser(String user) {
        Object[] parameters = {user};
        try(ResultSet result = processQuery("SELECT * FROM users WHERE User_Name = ?", parameters)){
            if(result.next()) {
                return createUser(result);
            } else {
                return null;
            }
        } catch(SQLException ex) {
            System.out.println("SQLException occurred in method getUser:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static User getUser(int id) {
        Object[] parameters = {id};
        try(ResultSet result = processQuery("SELECT * FROM users WHERE User_ID = ?", parameters)) {
            if(result.next()) {
                return createUser(result);
            } else {
                return null;
            }
        } catch(SQLException ex) {
            System.out.println("SQLException occurred in method getUser: ");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        try(ResultSet result = processQuery("SELECT * FROM users")) {

            boolean moreRows = result.next();
            while(moreRows) {
                users.add(createUser(result));
                moreRows = result.next();
            }
            return users;
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method getAllUsers:");
            System.out.println(ex.getMessage());
            return users;
        }
    }



    private static User createUser(ResultSet queryResult){
        try {
            int id = queryResult.getInt(1);
            String name = queryResult.getString(2);
            String password = queryResult.getString(3);
            LocalDateTime createDate = queryResult.getTimestamp(4).toLocalDateTime();
            String createdBy = queryResult.getString(5);
            LocalDateTime lastupdate = queryResult.getTimestamp(6).toLocalDateTime();
            String lastUpdatedBy = queryResult.getString(7);
            return new User(id, name, password, createDate, createdBy, lastupdate, lastUpdatedBy);
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method createUser:");
            System.out.println(ex.getMessage());
            return null;
        } catch(NullPointerException ex) {
            System.out.println("NullPointerException encountered in method createUser");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Create a new Appointment object reflecting the Appointment record info in the ResultSet passed in.
     * @param results A ResultSet containing at least one Appointment record, with a pointer currently placed immediately
     *                before the record to be converted into an Appointment object (which is the default state for any
     *                non-empty ResultSet).
     * @return A new Appointment object.
     * @throws SQLException Thrown in the event of a communication error with the SQL database via the ResultSet.
     */
    private static Appointment createAppointment(ResultSet results) throws SQLException{
        try{
            int id = results.getInt(1);
            String title = results.getString(2);
            String description = results.getString(3);
            String location = results.getString(4);
            String type = results.getString(5);
            LocalDateTime start = TimeConversion.timestampToLocalDateTime(results.getTimestamp(6));
            LocalDateTime end = TimeConversion.timestampToLocalDateTime(results.getTimestamp(7));
            LocalDateTime createDate = TimeConversion.timestampToLocalDateTime(results.getTimestamp(8));
            String createdBy = results.getString(9);
            LocalDateTime lastUpdate = TimeConversion.timestampToLocalDateTime(results.getTimestamp(10));
            String lastUpdatedBy = results.getString(11);
            int customerId = results.getInt(12);
            int userId = results.getInt(13);
            int contactId = results.getInt(14);
            return new Appointment(id, title, description, location, type, start, end, createDate, createdBy,
                    lastUpdate, lastUpdatedBy, customerId, userId, contactId);
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method createAppointment:");
            System.out.println(ex.getMessage());
            return null;
        } catch(NullPointerException ex) {
            System.out.println("A NullPointerException occurred in method createAppointment:");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Creates a new Contact object based on the info contained in the given ResultSet.
     * @param queryResult A ResultSet containing the results of a Contact record query.
     * @return A new Contact object, or null if the ResultSet passed in was empty.
     * @throws SQLException Thrown in the event of an error while interacting with the SQL ResultSet.
     */
    private static Contact createContact(ResultSet queryResult) throws SQLException{
        try {
            int id = queryResult.getInt(1);
            String contactName = queryResult.getString(2);
            String email = queryResult.getString(3);
            return new Contact(id, contactName, email);
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method createContact:");
            System.out.println(ex.getMessage());
            return null;
        } catch(NullPointerException ex) {
            System.out.println("NullPointerException encountered in method createContact:");
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
     * Removes the appointment corresponding to the given appointment ID from the database.
     * @param appointmentId The ID of the appointment we wish to remove from the database.
     * @return 1 if the appointment was successfully deleted, and 0 otherwise.
     */
    public static int removeAppointment(int appointmentId) {
        Object[] parameters = {appointmentId};
        try {
            return processUpdate("DELETE FROM appointments WHERE Appointment_ID = ?", parameters);
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method removeAppointment:");
            System.out.println(ex.getMessage());
            return 0;
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
            Object[] parameters = {name, address, postalCode, phone, fldId, ConnectionManager.getCurrentUser().getName(), customerId};
            for(int i = 1; i <= parameters.length; i++) {
                updateRecord.setObject(i, parameters[i - 1]);
            }
//            updateRecord.setString(7, Integer.toString(customerId));
            return updateRecord.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("An SQLException occurred in method updateCustomer:");
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    public static int updateAppointment(int appointmentId, String title, String description, String location, String type,
                                        LocalDateTime start, LocalDateTime end, int customerId, int userId, int contactId) {
        Object[] parameters = {title, description, location, type, Timestamp.valueOf(start), Timestamp.valueOf(end),
                ConnectionManager.getCurrentUser().getName(), customerId, userId, contactId, appointmentId};
        try{
            return processUpdate("UPDATE appointments SET Title = ?, Description = ?, Location = ?, " +
                    "Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, " +
                    "Contact_ID = ? WHERE Appointment_ID = ?", parameters);
        } catch(SQLException ex) {
            System.out.println("SQLException occurred in method updateAppointment:");
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

    private static int processUpdate(String updateString, Object[] parameters) throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        PreparedStatement query = connection.prepareStatement(updateString);
        for(int i = 1; i <= parameters.length; i++) {
            query.setObject(i, parameters[i - 1]);
        }
        return query.executeUpdate();
    }

    /**
     * Generates a list of available start times on a given date for scheduling appointments.
     * @param date The date we are retrieving available appointment times for.
     * @return An ArrayList of available times for scheduling, in order from soonest to latest.
     */
    public static ArrayList<LocalDateTime> getAvailableStartTimes(LocalDate date) {
        ArrayList<LocalDateTime> appointmentStartTimes = new ArrayList<>();
        LocalDateTime businessOpen = generateBusinessOpen(date);
        LocalDateTime businessClose = generateBusinessClose(date);
        int timeSlotLengthInMinutes = 30;
        // Generate appointment start times during business hours at 30 minutes intervals. As defined here, 30 minutes is
        // the shortest appointment available.
        LocalDateTime startTime = businessOpen;
        while(!startTimeOverlaps(getBookedAppointments(), startTime) && startTime.isBefore(businessClose)) {
            appointmentStartTimes.add(startTime);
            startTime = startTime.plusMinutes(timeSlotLengthInMinutes);
        }
        // Lambda usage 5 -- Filter out any overlapping appointment start times.
        appointmentStartTimes.removeIf(appointmentStart ->
                startTimeOverlaps(getBookedAppointments(), appointmentStart));
        return appointmentStartTimes;
    }

    /**
     * Generates a list of available appointment end times on a given date for a given appointment start time. Note that
     * it is necessary to first know the appointment start time because the validity of an end time depends on whether
     * the appointment starts before or after any already booked appointment.
     * @param date The date for which we are retrieving end appointment times for.
     * @param appointmentStart The start time of the appointment for which we are generating possible end times.
     * @return A list of available (that is, within business hours and not overlapping with any other existing
     * appointments) end times.
     */
    public static ArrayList<LocalDateTime> getAvailableEndTimes(LocalDate date, LocalDateTime appointmentStart) {
        ArrayList<LocalDateTime> appointmentEndTimes = new ArrayList<>();
        LocalDateTime businessOpen = generateBusinessOpen(date);
        LocalDateTime businessClose = generateBusinessClose(date);
        int timeSlotLengthInMinutes = 30;
        LocalDateTime endTime = appointmentStart.plusMinutes(timeSlotLengthInMinutes);
        while(!endTimeOverlaps(getBookedAppointments(), appointmentStart, endTime)
                && timeWithinBusinessHours(endTime, businessOpen, businessClose)) {
            appointmentEndTimes.add(endTime);
            endTime = endTime.plusMinutes(timeSlotLengthInMinutes);
        }
        return appointmentEndTimes;
    }

    /**
     * Combines a given date with predefined business hours to create a LocalDateTime object representing the opening
     * time of a particular date.
     * @param date The date for which we are defining an opening time.
     * @return A LocalDateTime object in the local timezone defining the time the business opens on the given date.
     */
    private static LocalDateTime generateBusinessOpen(LocalDate date) {
        return TimeConversion.toLocalTimeZone(
                LocalDateTime.of(date, AppointmentsController.getBusinessOpen()),
                AppointmentsController.getBusinessTimezone());
    }

    /**
     * Combines a given date with predefined business hours to create a LocalDateTime object representing the closing
     * time of a particular date.
     * @param date The date for which we are defining an closing time.
     * @return A LocalDateTime object in the local timezone defining the time the business closes on the given date.
     */
    private static LocalDateTime generateBusinessClose(LocalDate date) {
        return TimeConversion.toLocalTimeZone(
                LocalDateTime.of(date, AppointmentsController.getBusinessClose()),
                AppointmentsController.getBusinessTimezone());
    }

    private static boolean startTimeOverlaps(ArrayList<Appointment> bookedAppointments,
                                             LocalDateTime appointmentStart) {
        boolean appointmentOverlap = false;
        for(Appointment bookedAppointment : bookedAppointments) {
            appointmentOverlap = (appointmentStart.isEqual(bookedAppointment.getStart()) ||
                                 appointmentStart.isAfter(bookedAppointment.getStart())) &&
                                 appointmentStart.isBefore(bookedAppointment.getEnd());
        }
        return appointmentOverlap;
    }

    private static boolean endTimeOverlaps(ArrayList<Appointment> bookedAppointments,
                                           LocalDateTime appointmentStart, LocalDateTime appointmentEnd) {
        boolean appointmentOverlap = false;
        for(Appointment bookedAppointment : bookedAppointments) {
            if(appointmentStart.isBefore(bookedAppointment.getStart())) {
                appointmentOverlap = appointmentEnd.isAfter(bookedAppointment.getStart());
            }
        }
        return appointmentOverlap;
    }

    /**
     * Determines whether an appointment, defined by start and end time, is within business hours or not.
     * @param appointmentStart The appointment's start time.
     * @param appointmentEnd The appointment's end time.
     * @param businessOpen The business' open time.
     * @param businessClose The business' close time.
     * @return True if the appointment is within business hours, and False otherwise.
     */
    private static boolean appointmentWithinBusinessHours(LocalDateTime appointmentStart, LocalDateTime appointmentEnd,
                                                          LocalDateTime businessOpen, LocalDateTime businessClose) {
        return timeWithinBusinessHours(appointmentStart, businessOpen, businessClose) &&
                timeWithinBusinessHours(appointmentEnd, businessOpen, businessClose);
    }

    /**
     * Determines whether a given time falls within business hours.
     * @param time The time in question.
     * @param businessOpen The exact time that the business opens.
     * @param businessClose The exact time that the business closes.
     * @return True if the given time is within business hours and False otherwise.
     */
    private static boolean timeWithinBusinessHours(LocalDateTime time, LocalDateTime businessOpen, LocalDateTime businessClose) {
        return (time.isEqual(businessOpen) || time.isAfter(businessOpen)) &&
                (time.isEqual(businessClose) || time.isBefore(businessClose));
    }

    /**
     * Generates an ArrayList of Appointment objects representing the start and end times for all appointments currently
     * in the database. In the future, this method will accept a date by which to filter the records search, as
     * this method's runtime will scale linearly with the amount of appointments (including past ones) in the database.
     * @return An ArrayList representing the start times and dates for all currently scheduled appointments.
     */
    public static ArrayList<Appointment> getBookedAppointments() {
        ArrayList<Appointment> bookedAppointments = new ArrayList<>();
        try(ResultSet result = processQuery("SELECT * FROM appointments")) {
            boolean moreRows = result.next();
            while(moreRows) {
                bookedAppointments.add(createAppointment(result));
                moreRows = result.next();
            }
            return bookedAppointments;
        } catch(SQLException ex) {
            System.out.println("SQLException encountered in method getAllAppointments:");
            System.out.println(ex.getMessage());
            return bookedAppointments;
        }
    }
}
package dev.builditbear.model;

/**
 * Defines the object representation of a contact record in the SQL database.
 */
public class Contact {
    private int id;
    private String contactName;
    private String email;

    public Contact(int id, String contactName, String email) {
        this.id = id;
        this.contactName = contactName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }
}

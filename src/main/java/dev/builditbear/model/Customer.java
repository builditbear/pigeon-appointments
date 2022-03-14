package dev.builditbear.model;

public class Customer {
    private static int idCounter = 1;

    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private FirstLevelDivision firstLevelDivision;
    private Country country;

    public Customer(String name, String address, String postalCode, String phoneNumber) {
        this.id = idCounter;
        idCounter++;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        firstLevelDivision = null;
        country = null;

    }
}

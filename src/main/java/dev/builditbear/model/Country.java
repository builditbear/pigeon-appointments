package dev.builditbear.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Country {
    private final int id;
    private final String country;
    private final LocalDateTime createdDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;

    public Country(int id, String country, LocalDateTime createdDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy) {
        this.id = id;
        this.country = country;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
}
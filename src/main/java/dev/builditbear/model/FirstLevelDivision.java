package dev.builditbear.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class FirstLevelDivision {
    private final int id;
    private final String division;
    private final LocalDateTime dateTime;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;
    private final int countryId;

    public FirstLevelDivision(int id, String division, LocalDateTime dateTime, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int countryId) {
        this.id = id;
        this.division = division;
        this.dateTime = dateTime;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.countryId = countryId;
    }

    public int getId() {
        return id;
    }

    public String getDivision() {
        return division;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
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

    public int getCountryId() {
        return countryId;
    }
}

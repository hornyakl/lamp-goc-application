package com.docler.lamp.lampgocapplication.quest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Quest {
    private int id;

    private String name;

    private String description;

    private double latitude;

    private double longitude;

    private long experiencePoint;

    @JsonCreator
    public Quest(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("experience_point") long experiencePoint
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.experiencePoint = experiencePoint;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getExperiencePoint() {
        return experiencePoint;
    }
}

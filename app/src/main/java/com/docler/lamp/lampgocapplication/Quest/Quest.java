package com.docler.lamp.lampgocapplication.Quest;

public class Quest {
    private int id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private long experiencePoint;

    public Quest(int id, String name, String description, double latitude, double longitude, long experiencePoint) {
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

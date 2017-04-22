package com.docler.lamp.lampgocapplication.quest;

public class RelativeQuestPosition {
    private final double angle;
    private final double distance;
    private final Quest quest;

    public RelativeQuestPosition(double angle, double distance, Quest quest) {
        this.angle = angle;
        this.distance = distance;
        this.quest = quest;
    }

    public double getAngle() {
        return angle;
    }

    public double getDistance() {
        return distance;
    }

    public Quest getQuest() {
        return quest;
    }
}

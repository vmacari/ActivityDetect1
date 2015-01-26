package com.example.vmacari.utils;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by vmacari on 1/26/15.
 */
public class ActivityChangedEvent {

    private DetectedActivity activityType;
    private int confidence;


    private String description = "";
    private String typeString = "";


    public ActivityChangedEvent(DetectedActivity activityType, int confidence) {
        this.activityType = activityType;
        this.confidence = confidence;
    }

    public DetectedActivity getActivityType() {
        return activityType;
    }

    public void setActivityType(DetectedActivity activityType) {
        this.activityType = activityType;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}

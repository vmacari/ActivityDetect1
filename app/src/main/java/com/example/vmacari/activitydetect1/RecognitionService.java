package com.example.vmacari.activitydetect1;

import android.app.IntentService;
import android.content.Intent;

import com.example.vmacari.utils.ActivityChangedEvent;
import com.example.vmacari.utils.BusProvider;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by vmacari on 1/25/15.
 */
public class RecognitionService  extends IntentService {


    public RecognitionService() {
        super("ActivityRecognition");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            int confidence = mostProbableActivity.getConfidence();
            int activityType = mostProbableActivity.getType();

            String type;
            if (activityType == DetectedActivity.IN_VEHICLE) {
                type = "In Car";
            } else if (activityType == DetectedActivity.ON_FOOT) {
                type = "On Foot";
            } else if (activityType == DetectedActivity.ON_BICYCLE) {
                type = "By Bicycle";

            } else if (activityType == DetectedActivity.STILL) {
                type = "Still";
            } else if (activityType == DetectedActivity.UNKNOWN) {
                type = "Unknown";
            } else if (activityType == DetectedActivity.TILTING) {
                type = "Tilting";
            } else if (activityType == DetectedActivity.WALKING) {
                type = "Walking";

            } else if (activityType == DetectedActivity.RUNNING) {
                type = "Running";

            } else {
                type = "UnknownS";
            }

            ActivityChangedEvent activityChangeEvent = new ActivityChangedEvent(mostProbableActivity, confidence);
            activityChangeEvent.setDescription (String.format("Reported %s of confidence %d", type, confidence));
            activityChangeEvent.setTypeString (type);
            //BusProvider.getInstance().post(activityChangeEvent);
            BusProvider.post(activityChangeEvent);
        }

    }

}

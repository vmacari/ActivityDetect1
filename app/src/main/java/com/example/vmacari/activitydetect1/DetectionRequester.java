package com.example.vmacari.activitydetect1;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

public class DetectionRequester implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Context mContext;
    private final long maxUpdateInterval = 100; // ms
    private PendingIntent mActivityRecognitionPendingIntent;
    private final GoogleApiClient mActivityRecognitionGoogleApiClient;
    private boolean recognitionStarted = false;



    public DetectionRequester (Context context) {
        mContext = context;

        mActivityRecognitionPendingIntent = createRequestPendingIntent();

        mActivityRecognitionGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



    }


    public void startActivityRecognition () {

        //

        if (!recognitionStarted) {

            final PendingResult<Status> statusPendingResult = ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    mActivityRecognitionGoogleApiClient,
                    maxUpdateInterval,
                    mActivityRecognitionPendingIntent);

            recognitionStarted = true; // (statusPendingResult == Status.KA);
        }
    }

    public void stopActivityRecognition () {

        if (recognitionStarted) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mActivityRecognitionGoogleApiClient,
                    mActivityRecognitionPendingIntent);
            recognitionStarted = false;
        }
    }


    /**
     * Get a PendingIntent to send with the request to get activity recognition updates. Location
     * Services issues the Intent inside this PendingIntent whenever a activity recognition update
     * occurs.
     *
     * @return A PendingIntent for the IntentService that handles activity recognition updates.
     */
    private PendingIntent createRequestPendingIntent() {

        // If the PendingIntent already exists
        if (null != mActivityRecognitionPendingIntent) {

            // Return the existing intent
            return mActivityRecognitionPendingIntent;

            // If no PendingIntent exists
        } else {
            // Create an Intent pointing to the IntentService
            Intent intent = new Intent(mContext, RecognitionService.class);

            /*
             * Return a PendingIntent to start the IntentService.
             * Always create a PendingIntent sent to Location Services
             * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
             * again updates the original. Otherwise, Location Services
             * can't match the PendingIntent to requests made with it.
             */
            PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mActivityRecognitionPendingIntent = pendingIntent;
            return pendingIntent;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        startActivityRecognition();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
 /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {

            try {
                connectionResult.startResolutionForResult((Activity) mContext,
                       CONNECTION_FAILURE_RESOLUTION_REQUEST);

            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // display an error or log it here.
            }

        /*
         * If no resolution is available, display Google
         * Play service error dialog. This may direct the
         * user to Google Play Store if Google Play services
         * is out of date.
         */
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                    connectionResult.getErrorCode(),
                    (Activity) mContext,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (dialog != null) {
                dialog.show();
            }
        }
    }


    public void connect () {
        mActivityRecognitionGoogleApiClient.connect();
    }


    public void disconnect () {
        if (mActivityRecognitionGoogleApiClient.isConnected()) {

            stopActivityRecognition();
            mActivityRecognitionGoogleApiClient.disconnect();
        }
    }

}

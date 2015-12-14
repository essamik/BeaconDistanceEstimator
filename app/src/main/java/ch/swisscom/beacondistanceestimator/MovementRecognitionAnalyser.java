package ch.swisscom.beacondistanceestimator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

public class MovementRecognitionAnalyser implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public interface MovementRecognitionListener {
        void onMoving(int movementType);
    }
    protected static final String TAG = "MovementRecognition";

    private Context mContext;
    private MovementRecognitionListener mListener;
    /**
     * A receiver for DetectedActivity objects broadcast by the
     * {@code ActivityDetectionIntentService}.
     */
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    private GoogleApiClient mApiClient;

    public MovementRecognitionAnalyser(Context context, MovementRecognitionListener listener) {
        mContext = context;
        mListener = listener;
        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));

        mApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
        mApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()).setResultCallback(MovementRecognitionAnalyser.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {

        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent((Activity) mContext, ActivityRecognitionService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            int activityType = intent.getIntExtra(Constants.ACTIVITY_EXTRA, 0);
            mListener.onMoving(activityType);
        }
    }
}

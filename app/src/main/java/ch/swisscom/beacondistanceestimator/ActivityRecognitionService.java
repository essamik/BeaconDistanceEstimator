package ch.swisscom.beacondistanceestimator;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionService extends IntentService {

    protected static final String TAG = "ActivityRecService";

    private int mLastActivityType;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public ActivityRecognitionService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            int activityType = mostProbableActivity.getType();

            Log.d(TAG, "Activity: " + activityType);
            if (mLastActivityType != activityType) {
                // Broadcast the list of detected activities.
                Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

                localIntent.putExtra(Constants.ACTIVITY_EXTRA, activityType);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }

        }
    }
}
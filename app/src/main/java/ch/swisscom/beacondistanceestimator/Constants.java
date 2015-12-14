package ch.swisscom.beacondistanceestimator;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

public class Constants {

    public static final String PACKAGE_NAME = "ch.swisscom.beacondistanceestimator";

    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final int DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

}

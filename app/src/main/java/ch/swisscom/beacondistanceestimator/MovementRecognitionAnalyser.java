package ch.swisscom.beacondistanceestimator;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class MovementRecognitionAnalyser extends IntentService {

    public interface MovementRecognitionListener {
        void onMoving(int movementType);
    }

    private Context mContext;
    private MovementRecognitionListener mListener;

    private GoogleApiClient mApiClient;
    boolean mServiceConnected;

    public MovementRecognitionAnalyser(Context context, MovementRecognitionListener listener) {
        super(DistanceEstimator.class.getSimpleName());

        mListener = listener;

        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //TODO Init accelerometer
                        mServiceConnected = true;
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (mServiceConnected) {
            // If the incoming intent contains an update
            if (ActivityRecognitionResult.hasResult(intent)) {
                // Get the update
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                // Get the most probable activity
                DetectedActivity mostProbableActivity = result.getMostProbableActivity();

                int activityType = mostProbableActivity.getType();
                mListener.onMoving(activityType);

            }
        }
    }
}

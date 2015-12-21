package ch.swisscom.beacondistanceestimator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class AverageDistanceEstimator extends DistanceEstimator implements MovementRecognitionAnalyser.MovementRecognitionListener, SensorEventListener {

    private final String TAG = AverageDistanceEstimator.class.getSimpleName();

    private ArrayList<Double> mListRSSI;
    private Context mContext;
    private final static int MAX_ELEMENTS_IN_LIST = 10;
    private final static int MIN_ELEMENTS_IN_LIST = 2;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public AverageDistanceEstimator(Context context, double calibrationVal) {
        super(calibrationVal);
        mListRSSI = new ArrayList<>();
        mContext = context;
        //new MovementRecognitionAnalyser(context, this);

        //Init sensor manager to listen to movements
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (mListRSSI.size() > MIN_ELEMENTS_IN_LIST) cutLastMeasures(MAX_ELEMENTS_IN_LIST/2);
        }
        Log.d(TAG, "Step detected, ");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void addRSSI(double newMeasure) {
        if (mListRSSI.size() >= MAX_ELEMENTS_IN_LIST) {
            mListRSSI.remove(0);
        }
        mListRSSI.add(newMeasure);
    }

    public void cutLastMeasures(int nbMeasure) {
        if (mListRSSI.size() > nbMeasure) {
            mListRSSI.subList(0, nbMeasure).clear();
        }
    }

    public double getAveragedDistance() {
        double sum = 0;
        for (Double measure : mListRSSI) {
            sum += measure;
        }

        double avgRSSI = (sum/ mListRSSI.size());
        return super.calculateDistance(avgRSSI, mCalibrationVal);
    }

    @Override
    public void onMoving(int movementType) {


        switch(movementType) {

            case DetectedActivity.ON_FOOT: {
                cutLastMeasures(MAX_ELEMENTS_IN_LIST/2);
            }
            break;

            case DetectedActivity.RUNNING: {
                cutLastMeasures(MAX_ELEMENTS_IN_LIST/5);

            }
            break;
            case DetectedActivity.WALKING: {
                cutLastMeasures(MAX_ELEMENTS_IN_LIST/2);
            }
            break;
            case DetectedActivity.STILL: {
                //Do nothing
            }
            break;
            default: {
                //Do nothing
            }
            break;
        }
        //Log.d(TAG, "onMoving: " + Constants.getActivityString(mContext, movementType) + " Length : " + mListRSSI.size());
    }
}

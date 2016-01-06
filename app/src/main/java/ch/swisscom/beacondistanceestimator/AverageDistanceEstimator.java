package ch.swisscom.beacondistanceestimator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class AverageDistanceEstimator extends DistanceEstimator implements SensorEventListener {

    private final String TAG = AverageDistanceEstimator.class.getSimpleName();

    private ArrayList<Double> mListRSSI;
    private double mCalibrationVal;
    private boolean mConfigCorrection;

    private Context mContext;

    private final static int MAX_ELEMENTS_IN_LIST = 20;
    private final static int MIN_ELEMENTS_IN_LIST = 2;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    /**
     * Initialize a distance estimator working on a running average
     * @param context The app context
     * @param calibrationVal The RSSI at a distance of 1m
     * @param configCorrection Indicate if the RSSI correction has to be applied in case of specific transmission power
     */
    public AverageDistanceEstimator(Context context, double calibrationVal, boolean configCorrection) {
        mListRSSI = new ArrayList<>();
        mContext = context;
        mCalibrationVal = calibrationVal;
        mConfigCorrection =  configCorrection;

        //Init sensor manager to listen to movements
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Triggered when the smartphone sensor detects a movement.
     * Used to remove the old measures from the RSSI list.
     * @param event The type of movement
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (mListRSSI.size() > MIN_ELEMENTS_IN_LIST) cutLastMeasures(mListRSSI.size()/2);
        }
        Log.d(TAG, "Step detected, ");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    /**
     * Add a RSSI measure to the list.
     * @param newMeasure The new RSSI value
     */
    public void addRSSI(double newMeasure) {
        if (mListRSSI.size() >= MAX_ELEMENTS_IN_LIST) {
            mListRSSI.remove(0);
        }
        mListRSSI.add(newMeasure);
    }

    /**
     * Remove the n oldest values
     * @param nbMeasure The number of measures to remove from the beginning of the list
     */
    public void cutLastMeasures(int nbMeasure) {
        if (mListRSSI.size() > nbMeasure) {
            mListRSSI.subList(0, nbMeasure).clear();
        }
    }

    /**
     * Reinitialize the RSSI list
     */
    public void clearRSSIList() {
        mListRSSI = new ArrayList<>();
    }

    /**
     * Calculate the distance based on the RSSI sample actually in memory.
     * @return The distance in meter between the smartphone and the iBeacon.
     */
    public double getAveragedDistance() {
        double sum = 0;
        for (Double measure : mListRSSI) {
            sum += measure;
        }

        //Discard the minimum and the maximum (top 10% and bottom 10% only when the buffer is full)
        boolean discardMinMax = (mListRSSI.size() == MAX_ELEMENTS_IN_LIST);
        if (discardMinMax)  {
            sum -= Collections.max(mListRSSI);
            sum -= Collections.min(mListRSSI);
        }

        double avgRSSI = (sum/ (discardMinMax ? mListRSSI.size()-2 : mListRSSI.size()));
        return super.calculateDistance(avgRSSI, mCalibrationVal, mConfigCorrection);
    }

    /**
     * Return the number of RSSI in memory
     * @return The nb of RSSI
     */
    public int getSampleSize() {
        return mListRSSI.size();
    }
}

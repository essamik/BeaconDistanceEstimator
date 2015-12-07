package ch.swisscom.beacondistanceestimator;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

public class AverageDistanceEstimator extends DistanceEstimator implements MovementRecognitionAnalyser.MovementRecognitionListener {

    private List<Double> mListRSSI;

    AverageDistanceEstimator(Context context, double calibrationVal) {
        super(calibrationVal);
        mListRSSI = new ArrayList<>();
        new MovementRecognitionAnalyser(context, this);
    }

    public void addRSSI(double newMeasure) {
        if (mListRSSI.size() >= 10) {
            mListRSSI.remove(0);
        }
        mListRSSI.add(newMeasure);
    }

    public void cutLastMeasures(int nbMeasure) {

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
            case DetectedActivity.RUNNING: {
                cutLastMeasures(9);
                //TODO Cut the last 9 averagedMeasurements

            }
            break;
            case DetectedActivity.WALKING: {
                cutLastMeasures(5);
                //TODO Cut the last 5 averagedMeasurements
            }
            break;
            case DetectedActivity.STILL: {
                //TODO Do nothing
            }
            break;
            default: {
                //TODO ?

            }
            break;
        }

    }
}

package ch.swisscom.beacondistanceestimator;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class AverageDistanceEstimator extends DistanceEstimator implements MovementRecognitionAnalyser.MovementRecognitionListener {

    private ArrayList<Double> mListRSSI;
    private Context mContext;
    private final static int MAX_ELEMENTS_IN_LIST = 10;
    private int mLastActivityType;

    public AverageDistanceEstimator(Context context, double calibrationVal) {
        super(calibrationVal);
        mListRSSI = new ArrayList<>();
        mContext = context;
        new MovementRecognitionAnalyser(context, this);
    }

    public void addRSSI(double newMeasure) {
        if (mListRSSI.size() >= MAX_ELEMENTS_IN_LIST) {
            mListRSSI.remove(0);
        }
        mListRSSI.add(newMeasure);
    }

    public void cutLastMeasures(int nbMeasure) {
        if (mListRSSI.size() >= MAX_ELEMENTS_IN_LIST) {
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
        Toast.makeText(mContext, "onMoving: " + Constants.getActivityString(mContext, movementType),
                Toast.LENGTH_SHORT).show();

        switch(movementType) {

            case DetectedActivity.ON_FOOT: {
                cutLastMeasures(MAX_ELEMENTS_IN_LIST/2);
            }
            break;

            case DetectedActivity.RUNNING: {
                cutLastMeasures(MAX_ELEMENTS_IN_LIST-1);

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

    }
}

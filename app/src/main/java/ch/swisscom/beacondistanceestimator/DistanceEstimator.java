package ch.swisscom.beacondistanceestimator;

import java.util.HashMap;
import java.util.Map;

public class DistanceEstimator {

    protected double mCalibrationVal;

    private static Map<Double, Double> sCalibrationToCorrection;
    static {
        sCalibrationToCorrection = new HashMap();
        sCalibrationToCorrection.put(-115d, 0.6695652173913);
        sCalibrationToCorrection.put(-84d, 0.91666666666667);
        sCalibrationToCorrection.put(-81d, 0.95061728395062);
        sCalibrationToCorrection.put(-77d, 1d);
        sCalibrationToCorrection.put(-72d, 1.06944444444444);
        sCalibrationToCorrection.put(-69d, 1.11594202898551);
        sCalibrationToCorrection.put(-65d, 1.18461538461538);
        sCalibrationToCorrection.put(-59d, 1.30508474576271);
    }


    DistanceEstimator(double calibrationVal) {
        mCalibrationVal = calibrationVal;
    }

    public static double calculateDistance(double rssi, double calibrationValue) {


        double correctedRSSI = rssi * (sCalibrationToCorrection.get(calibrationValue));
        double distance = calculateDistance(correctedRSSI);

        //Set minimum possible distance
        if (distance <= 0) distance = 0.01;
        return Math.round(distance * 100.0) / 100.0;
    }

    /**
     * Compute the distance based on a given beacon RSSI
     * @param x The RSSI value
     * @return The distance between the device and the beacon
     */
    private static double calculateDistance(double x) {
        return -2.523223e+02
                -1.487040e+01 * x
                -3.204729e-01 * Math.pow(x,2)
                -2.968497e-03 * Math.pow(x,3)
                -9.817707e-06 * Math.pow(x,4);
    }

    /** Other models */

    private static double calculateDistanceForSmallAppartment(double x) {
        x = Math.abs(x);
        double a = -1.809821e-01;
        double b = 5.462871e-02 * x;
        double c = -1.654253e-03 * Math.pow(x,2);
        double d = 1.817589e-06 * Math.pow(x,3);
        double e = 2.085460e-07 * Math.pow(x,4);
        return a+b+c+d+e;
    }



}

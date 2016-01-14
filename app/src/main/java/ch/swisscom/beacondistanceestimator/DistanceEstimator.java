package ch.swisscom.beacondistanceestimator;

import java.util.HashMap;
import java.util.Map;

public class DistanceEstimator {

    private static Map<Double, Double> sConfigurationCorrection;
    static {
        sConfigurationCorrection = new HashMap();
        sConfigurationCorrection.put(-115d, 38d);
        sConfigurationCorrection.put(-84d, 7d);
        sConfigurationCorrection.put(-81d, 4d);
        sConfigurationCorrection.put(-77d, 0d);
        sConfigurationCorrection.put(-72d, -5d);
        sConfigurationCorrection.put(-69d, -8d);
        sConfigurationCorrection.put(-65d, -12d);
        sConfigurationCorrection.put(-59d, -18d);
    }

    /**
     * Compute an estimation of the distance based on the RSSI and the calibrated value for 1 meter of the beacon
     * @param  rssi The signal strength
     * @param calibrationValue The calibrated RSSI at a distance of 1 meter
     * @param configCorrection Indicate if a correction of the RSSI is needed due to a specific transmission power
     * @return The distance between the smartphone and the iBeacon in meters
     */
    public static double calculateDistance(double rssi, double calibrationValue, boolean configCorrection) {
        if (configCorrection) rssi = rssi + (sConfigurationCorrection.get(calibrationValue));
        double distance = calculateDistance(rssi);

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
         return -6.492139e+01
                -3.883648e+00 * x
                -8.258342e-02 * Math.pow(x,2)
                -7.131272e-04 * Math.pow(x,3)
                -1.913343e-06 * Math.pow(x,4);
    }

    /** Other models */

    private static double calculateDistanceFromCeiling(double x) {
         return -6.188210e+02
                -2.440569e+01 * x
                -3.226527e-01 * Math.pow(x,2)
                -1.433933e-03 * Math.pow(x,3);
    }
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

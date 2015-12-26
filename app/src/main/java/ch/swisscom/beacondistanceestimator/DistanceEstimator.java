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
        //double correctedRSSI = Math.abs(rssi) * (sCalibrationToCorrection.get(calibrationValue));
        //double distance = calculateForAltstetten(correctedRSSI);

        double correctedRSSI = rssi * (sCalibrationToCorrection.get(calibrationValue));
       double distance = calculateForP31Floor(correctedRSSI);

        //Set minimum possible distance
        if (distance <= 0) distance = 0.01;
        return Math.round(distance * 100.0) / 100.0;
    }

    private static double calculateForP31(double x) {
        return 2.967470e-02
                - 1.411623e-02*x
                - 3.636727e-03*Math.pow(x,2)
                - 1.792129e-04*Math.pow(x,3)
                - 3.269059e-06*Math.pow(x,4)
                - 2.056007e-08*Math.pow(x,5);
    }

    private static double calculateForAltstetten(double x) {
         double a = -1.809821e-01;
         double b = 5.462871e-02 * x;
         double c = -1.654253e-03 * Math.pow(x,2);
         double d = 1.817589e-06 * Math.pow(x,3);
         double e = 2.085460e-07 * Math.pow(x,4);
         return a+b+c+d+e;
    }

    private static double calculateForP31Floor(double x) {
        /** V1
        return -1.382795e-01
                + 9.271514e-02*x
                + 6.830602e-03*Math.pow(x,2)
                + 1.593348e-04*Math.pow(x,3)
                + 1.222865e-06*Math.pow(x,4);
         */
        /** V2
        return 8.362029e-02
                + 4.713418e-02*x
                + 4.161346e-03*Math.pow(x,2)
                + 1.160100e-04*Math.pow(x,3)
                + 1.005037e-06*Math.pow(x,4);
         */

        /** V3
        return 1.422656e+00
                + 6.432832e-01 *x
                + 3.940699e-02*Math.pow(x,2)
                + 9.271434e-04*Math.pow(x,3)
                + 9.219714e-06*Math.pow(x,4)
                + 3.061494e-08*Math.pow(x,5);
    }
         */
    /** V4*/
    return -2.523223e+02
            -1.487040e+01*x
            -3.204729e-01*Math.pow(x,2)
            -2.968497e-03*Math.pow(x,3)
            -9.817707e-06*Math.pow(x,4);
    }

}

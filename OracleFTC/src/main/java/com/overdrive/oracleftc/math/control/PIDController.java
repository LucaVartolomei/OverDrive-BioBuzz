package com.overdrive.oracleftc.math.control;

@SuppressWarnings("unused")
public class PIDController {
    public double p, i, d;
    private long lastTimestamp = 0;
    private double integral = 0;
    private double lastError = 0;

    public PIDController(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }

    public void reset() {
        lastTimestamp = 0;
        integral = 0;
        lastError = 0;
    }

    public double update(double target, double value) {
        double error = target - value;
        long timestamp = System.nanoTime();
        double derivative = 0;
        if (lastTimestamp != 0) {
            double deltaTime = (timestamp - lastTimestamp) / 1E9;
            integral += deltaTime * error;
            if (deltaTime > 1E-6) derivative = (error - lastError) / deltaTime;

        }
        lastError = error;
        lastTimestamp = timestamp;
        return error * p + integral * i + derivative * d;
    }


}

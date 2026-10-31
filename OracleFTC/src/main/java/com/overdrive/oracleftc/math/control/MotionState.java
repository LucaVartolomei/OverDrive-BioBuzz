package com.overdrive.oracleftc.math.control;

public class MotionState {
    public final double position, velocity, acceleration;

    public MotionState(double position, double velocity, double acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }
}

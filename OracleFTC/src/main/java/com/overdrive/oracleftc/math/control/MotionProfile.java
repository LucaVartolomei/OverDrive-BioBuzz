package com.overdrive.oracleftc.math.control;

public abstract class MotionProfile {
    abstract MotionState getMotionState(double distance, double time);
}

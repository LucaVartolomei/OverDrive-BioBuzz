package com.overdrive.oracleftc.math.control;

import com.overdrive.oracleftc.math.DualNum;
import com.overdrive.oracleftc.math.Time;

public abstract class MotionProfile {
    abstract DualNum<Time> getMotionState(double distance, double time);
}

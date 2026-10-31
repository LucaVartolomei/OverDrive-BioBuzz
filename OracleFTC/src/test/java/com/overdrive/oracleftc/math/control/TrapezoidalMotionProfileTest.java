package com.overdrive.oracleftc.math.control;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TrapezoidalMotionProfileTest {


    @Test
    public void validTrapezoidalMotionProfile()
    {
        TrapezoidalMotionProfile motionProfile = new TrapezoidalMotionProfile(1.0, 0.8, 0.6);
        double distance = 1.0;
        MotionState motionState;

        long startTime = System.nanoTime();
        boolean valid=true;
        do {
            motionState=motionProfile.getMotionState(distance,(System.nanoTime()-startTime)/1E9);
            if(motionState.velocity<0
                || motionState.acceleration> motionProfile.maxAcceleration
                || motionState.acceleration< -motionProfile.maxDeceleration
                || Math.abs(motionState.velocity)> motionProfile.maxVelocity) valid=false;
        }while(Math.abs(motionState.position-distance)>0.001);
        assertTrue("TrapezoidMotionProfile returned an invalid motion state for movement", valid);
    }
}

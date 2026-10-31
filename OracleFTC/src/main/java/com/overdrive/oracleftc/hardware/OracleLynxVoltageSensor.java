package com.overdrive.oracleftc.hardware;

import android.content.Context;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxVoltageSensor;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.LastKnown;

public class OracleLynxVoltageSensor extends LynxVoltageSensor {
    public OracleLynxVoltageSensor(Context context, LynxModule module) throws RobotCoreException, InterruptedException {
        super(context, module);
    }
    private LastKnown<Double> lastKnownVoltage = new LastKnown<>(5000);
    private final Object lastKnownVoltageLock = new Object();

    public void setCacheFreshness(int freshness)
    {
        LastKnown<Double> newLastKnown=new LastKnown<>(freshness);
        newLastKnown.setValue(lastKnownVoltage.getNonTimedValue());
        synchronized (lastKnownVoltageLock)
        {
            lastKnownVoltage=newLastKnown;
        }
    }
    /**
     * Gets the cached voltage of this voltage sensor.
     * @return
     */
    @SuppressWarnings({"unused"})
    public double getCachedVoltage()
    {
        Double voltage;
        synchronized (lastKnownVoltageLock)
        {
            voltage = lastKnownVoltage.getValue();
        }
        if(voltage==null)
            return getVoltage();
        return voltage;
    }
    @Override
    public double getVoltage() {
        double voltage = super.getVoltage();
        synchronized (lastKnownVoltageLock)
        {
            lastKnownVoltage.setValue(voltage);
        }
        return voltage;
    }
}

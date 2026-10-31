package com.overdrive.oracleftc.hardware.i2c;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.configuration.LynxConstants;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

@I2cDeviceType
@DeviceProperties(name = "Oracle REV internal IMU (BHI260AP)", xmlTag = "OracleBHI260IMU", builtIn = true)
public class OracleBHI260IMU extends BHI260IMU implements OracleI2cInterleavableDevice {
    public OracleBHI260IMU(I2cDeviceSynchSimple i2cDeviceSynchSimple, boolean deviceClientIsOwned) {
        super(i2cDeviceSynchSimple, deviceClientIsOwned);
    }
    @Override
    public void scheduleInterleavedRunnable(Runnable runnable) {
        ((OracleI2cInterleavableDevice)deviceClient).scheduleInterleavedRunnable(runnable);
    }
}

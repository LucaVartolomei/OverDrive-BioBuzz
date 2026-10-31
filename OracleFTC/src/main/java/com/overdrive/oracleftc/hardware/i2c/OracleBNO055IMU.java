package com.overdrive.oracleftc.hardware.i2c;

import static com.qualcomm.robotcore.hardware.ControlSystem.REV_HUB;

import com.qualcomm.hardware.lynx.LynxEmbeddedBNO055IMUNew;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

@I2cDeviceType
@DeviceProperties(name = "Oracle REV internal IMU (BNO055)", xmlTag = "OracleBNO055IMU", description = "Oracle enhanced BNO055 IMU", builtIn = true, compatibleControlSystems = REV_HUB)

public class OracleBNO055IMU extends LynxEmbeddedBNO055IMUNew implements OracleI2cInterleavableDevice {

    /**
     * This constructor is called internally by the FTC SDK.
     *
     * @param deviceClient
     * @param deviceClientIsOwned
     */
    public OracleBNO055IMU(I2cDeviceSynchSimple deviceClient, boolean deviceClientIsOwned) {
        super(deviceClient, deviceClientIsOwned);
    }

    @Override
    public void scheduleInterleavedRunnable(Runnable runnable) {
        ((OracleI2cInterleavableDevice)deviceClient).scheduleInterleavedRunnable(runnable);
    }
}

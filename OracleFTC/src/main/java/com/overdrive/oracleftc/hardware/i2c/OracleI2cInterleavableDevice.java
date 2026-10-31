package com.overdrive.oracleftc.hardware.i2c;

import com.qualcomm.hardware.lynx.commands.LynxMessage;

public interface OracleI2cInterleavableDevice {
    void scheduleInterleavedRunnable(Runnable runnable);
}

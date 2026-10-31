package com.overdrive.oracleftc.hardware;

import android.content.Context;

import com.qualcomm.hardware.lynx.LynxAnalogInputController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.exception.RobotCoreException;

public class OracleLynxAnalogInputController extends LynxAnalogInputController {
    public OracleLynxAnalogInputController(Context context, LynxModule module) throws RobotCoreException, InterruptedException {
        super(context, module);
    }
}

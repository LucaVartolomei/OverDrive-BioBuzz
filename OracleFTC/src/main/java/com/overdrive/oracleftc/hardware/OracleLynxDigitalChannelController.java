package com.overdrive.oracleftc.hardware;

import android.content.Context;

import com.qualcomm.hardware.lynx.LynxDigitalChannelController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.exception.RobotCoreException;

public class OracleLynxDigitalChannelController extends LynxDigitalChannelController {
    public OracleLynxDigitalChannelController(Context context, LynxModule module) throws RobotCoreException, InterruptedException {
        super(context, module);
    }
}

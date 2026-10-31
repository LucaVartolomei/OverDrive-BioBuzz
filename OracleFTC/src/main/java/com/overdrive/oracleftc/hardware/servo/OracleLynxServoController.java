package com.overdrive.oracleftc.hardware.servo;

import android.content.Context;

import com.qualcomm.hardware.lynx.LynxController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.LynxServoController;
import com.qualcomm.hardware.lynx.LynxUsbUtil;
import com.qualcomm.hardware.lynx.commands.core.LynxGetServoEnableCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetServoEnableResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetServoPulseWidthCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetServoPulseWidthResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxSetServoConfigurationCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetServoEnableCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetServoPulseWidthCommand;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.configuration.LynxConstants;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.ServoConfigurationType;
import com.qualcomm.robotcore.util.LastKnown;
import com.qualcomm.robotcore.util.Range;

public class OracleLynxServoController extends LynxServoController {

    public OracleLynxServoController(Context context, LynxModule module) throws RobotCoreException, InterruptedException {
        super(context, module);
    }
}

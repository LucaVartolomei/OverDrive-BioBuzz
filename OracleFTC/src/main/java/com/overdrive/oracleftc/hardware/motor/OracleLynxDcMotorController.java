package com.overdrive.oracleftc.hardware.motor;

import android.content.Context;

import com.qualcomm.hardware.lynx.LynxController;
import com.qualcomm.hardware.lynx.LynxDcMotorController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.LynxUsbUtil;
import com.qualcomm.hardware.lynx.commands.LynxCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetADCCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetADCResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorChannelCurrentAlertLevelCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorChannelCurrentAlertLevelResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorChannelEnableCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorChannelEnableResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorChannelModeCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorChannelModeResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorConstantPowerCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorConstantPowerResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorEncoderPositionCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorEncoderPositionResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorPIDControlLoopCoefficientsCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorPIDControlLoopCoefficientsResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorPIDFControlLoopCoefficientsCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorPIDFControlLoopCoefficientsResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorTargetPositionCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorTargetPositionResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorTargetVelocityCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetMotorTargetVelocityResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxIsMotorAtTargetCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxIsMotorAtTargetResponse;
import com.qualcomm.hardware.lynx.commands.core.LynxResetMotorEncoderCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorChannelCurrentAlertLevelCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorChannelEnableCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorChannelModeCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorConstantPowerCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorPIDControlLoopCoefficientsCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorPIDFControlLoopCoefficientsCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorTargetPositionCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorTargetVelocityCommand;
import com.qualcomm.hardware.lynx.commands.standard.LynxNack;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.exception.TargetPositionNotSetException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.HardwareDeviceHealthImpl;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.ExpansionHubMotorControllerParamsState;
import com.qualcomm.robotcore.hardware.configuration.LynxConstants;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.LastKnown;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;
import com.overdrive.oracleftc.hardware.OracleLynxModule;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.firstinspires.ftc.robotcore.internal.system.Misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OracleLynxDcMotorController extends LynxDcMotorController {
    public OracleLynxDcMotorController(Context context, LynxModule module) throws RobotCoreException, InterruptedException {
        super(context, module);
    }
}

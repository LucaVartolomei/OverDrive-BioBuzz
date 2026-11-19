package org.firstinspires.ftc.teamcode.Subsystem;


import com.overdrive.oracleftc.commands.Command;
import com.overdrive.oracleftc.hardware.OracleLynxVoltageSensor;
import com.overdrive.oracleftc.hardware.subsystem.Subsystem;
import com.overdrive.oracleftc.utils.input.OracleGamepad;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MecanumSubsystem extends Subsystem {
    private final DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private final OracleLynxVoltageSensor voltageSensor;
    private final IMU imu;

    public MecanumSubsystem(OpMode opMode){
        super(opMode);

        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        //to do: reverse motors ?

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        voltageSensor = (OracleLynxVoltageSensor) hardwareMap.voltageSensor.iterator().next();

        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));

        imu.initialize(parameters);
    }

    public void setMotorPowers(double frontRightPower, double frontLeftPower, double backRightPower, double backLeftPower){
        double voltage = voltageSensor.getVoltage();

        frontRight.setPower(frontRightPower * (12.0 / voltage));
        frontLeft.setPower(frontLeftPower * (12.0 / voltage));
        backRight.setPower(backRightPower * (12.0 / voltage));
        backLeft.setPower(backLeftPower * (12.0 / voltage));
    }

    public Command drive(OracleGamepad driveGamepad){
        return Command.builder()
                .update(()->{
                    double y = -driveGamepad.left_stick.get().y;
                    double x = driveGamepad.left_stick.get().x;
                    double rx = driveGamepad.right_stick.get().x;

                    // This button choice was made so that it is hard to hit on accident,
                    // it can be freely changed based on preference.
                    // The equivalent button is start on Xbox-style controllers.
                    // if (driveGamepad.options) {
                    //     imu.resetYaw();
                    // }

                    double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

                    double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
                    double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

                    rotX = rotX * 1.1;  // Counteract imperfect strafing

                    double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
                    double frontLeftPower = (rotY + rotX + rx) / denominator;
                    double backLeftPower = (rotY - rotX + rx) / denominator;
                    double frontRightPower = (rotY - rotX - rx) / denominator;
                    double backRightPower = (rotY + rotX - rx) / denominator;

                    setMotorPowers(frontRightPower, frontLeftPower, backRightPower, backLeftPower);
                })
                .build();
    }
}

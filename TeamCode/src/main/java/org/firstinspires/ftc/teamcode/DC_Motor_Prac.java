package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(name = "DC Motor Prac", group = "Tutorials")
public class DC_Motor_Prac extends LinearOpMode {

    private DcMotor motor;

    @Override
    public void runOpMode() {

        motor = hardwareMap.get(DcMotor.class, "motor");
        //motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized. Ready to start!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double tgtPower = -gamepad1.left_stick_y;
            double motorPower;
            if (Math.abs(tgtPower) > 0.05) {
                motorPower=tgtPower;
            }
            else{
                motorPower=0;
            }

            motor.setPower(motorPower);

            telemetry.addData("Joystick Y", gamepad1.left_stick_y);
            telemetry.addData("Motor Power", tgtPower);
            telemetry.update();
        }
    }
}



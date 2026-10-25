package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp
public class shootTest extends LinearOpMode {

    private DcMotor intakeMotor;
    private DcMotor shooterMotor;   

    @Override
    public void runOpMode() {

        shooterMotor = hardwareMap.get(shootTest.class, "shooterMotor");
        intakeMotor = hardwareMap.get(shootTest.class, "intakeMotor");

        telemetry.addData("Status", "Initialized. Ready to start!"); 
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean intakePower = gamepad.a;
            boolean shooterPower = gamepad.b;

            intakeMotor.setPower(intakePower);
            shooterMotor.setPower(shooterPower);

            if(intakePower = 1){  
                telemetry.addData("intake working");
                telemetry.update();
            }else{
                telemetry.addData("intake stopped");
                telemetry.update();
            }

            if(shooterPower = 1){  
                telemetry.addData("shooter working");
                telemetry.update();
            }else{
                telemetry.addData("shooter stopped");
                telemetry.update();
            }
        
            /*
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
            if we need to use the joysticks
            */


        }
    }
}
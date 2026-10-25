package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Intake Feeder OpMode", group = "TeleOp")
public class intakeFeederOpMode extends LinearOpMode {

    private DcMotor intakeMotor;
    private DcMotor shooterMotor;

    @Override
    public void runOpMode() {

        shooterMotor = hardwareMap.get(DcMotor.class, "shooterMotor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        telemetry.addData("Status", "Initialized. Ready to start!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean intake = gamepad1.a;
            boolean shooter = gamepad1.b;

            if (intake) {
                intakeMotor.setPower(1.0);
                telemetry.addData("Intake Motor", "Çalışıyor (Power: 1.0)");
            } else {
                intakeMotor.setPower(0.0);
                telemetry.addData("Intake Motor", "Durduruldu");
            }

            if (shooter) {
                shooterMotor.setPower(1.0);
                telemetry.addData("Shooter Motor", "Çalışıyor (Power: 1.0)");
            } else {
                shooterMotor.setPower(0.0);
                telemetry.addData("Shooter Motor", "Durduruldu");
            }

            telemetry.update();
        }
    }
}

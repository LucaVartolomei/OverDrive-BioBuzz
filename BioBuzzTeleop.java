package MasterCode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "BioBuzzTeleop", group = "Drive")
public class BioBuzzTeleop extends OpMode {
    private DcMotorEx frontLeftMotor;
    private DcMotorEx frontRightMotor;
    private DcMotorEx backLeftMotor;
    private DcMotorEx backRightMotor;

    @Override
    public void init() {
        frontLeftMotor = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRightMotor = hardwareMap.get(DcMotorEx.class, "backRight");

        frontLeftMotor.setDirection(DcMotorEx.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorEx.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorEx.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorEx.Direction.REVERSE);

        frontLeftMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        setMotorPower(0, 0, 0, 0);
    }

    @Override
    public void loop() {
        // Driver 1: left stick drives/translates; right stick rotates.
        double y = -gamepad1.left_stick_y / 2.0;
        double x = gamepad1.left_stick_x / 2.0;
        double rotation = gamepad1.right_stick_x / 2.0;

        // Right trigger boosts translation and rotation.
        double boost = gamepad1.right_trigger / 2.0;
        y += y * boost;
        x += x * boost;
        rotation += rotation * boost;

        // D-pad overrides the left stick for cardinal movement.
        if (gamepad1.dpad_up) {
            x = 0;
            y = 0.5 * (1.0 + boost);
        } else if (gamepad1.dpad_down) {
            x = 0;
            y = -0.5 * (1.0 + boost);
        } else if (gamepad1.dpad_left) {
            x = -0.5 * (1.0 + boost);
            y = 0;
        } else if (gamepad1.dpad_right) {
            x = 0.5 * (1.0 + boost);
            y = 0;
        }

        // Mecanum drive calculations.
        double frontLeftPower = y + x + rotation;
        double frontRightPower = y - x - rotation;
        double backLeftPower = y - x + rotation;
        double backRightPower = y + x - rotation;

        // Normalize all powers together to preserve their ratios.
        double maxPower = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
        ));

        setMotorPower(
                frontLeftPower / maxPower,
                frontRightPower / maxPower,
                backLeftPower / maxPower,
                backRightPower / maxPower
        );
    }

    @Override
    public void stop() {
        setMotorPower(0, 0, 0, 0);
    }

    private void setMotorPower(double frontLeftPower, double frontRightPower,
                               double backLeftPower, double backRightPower) {
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);
    }
}

package com.overdrive.oracleftc.utils.input;

@SuppressWarnings("unused")
public class OracleGamepad {
    public final Button a, b, x, y, cross, square, circle, triangle, dpad_up, dpad_down, dpad_left, dpad_right, left_bumper, right_bumper;
    public final Trigger left_trigger, right_trigger;
    public final Joystick left_stick, right_stick;

    public OracleGamepad(com.qualcomm.robotcore.hardware.Gamepad gamepad) {
        a = new Button(() -> gamepad.a);
        b = new Button(() -> gamepad.b);
        x = new Button(() -> gamepad.x);
        y = new Button(() -> gamepad.y);
        dpad_up = new Button(() -> gamepad.dpad_up);
        dpad_down = new Button(() -> gamepad.dpad_down);
        dpad_left = new Button(() -> gamepad.dpad_left);
        dpad_right = new Button(() -> gamepad.dpad_right);
        left_bumper = new Button(() -> gamepad.left_bumper);
        right_bumper = new Button(() -> gamepad.right_bumper);
        cross = new Button(() -> gamepad.cross);
        square = new Button(() -> gamepad.square);
        circle = new Button(() -> gamepad.circle);
        triangle = new Button(() -> gamepad.triangle);
        left_trigger = new Trigger(() -> (double) gamepad.left_trigger);
        right_trigger = new Trigger(() -> (double) gamepad.right_trigger);
        left_stick = new Joystick(() -> new JoystickData(gamepad.left_stick_x, gamepad.left_stick_y, gamepad.left_stick_button));
        right_stick = new Joystick(() -> new JoystickData(gamepad.right_stick_x, gamepad.right_stick_y, gamepad.right_stick_button));
    }

    public void process() {
        a.process();
        b.process();
        x.process();
        y.process();
        dpad_up.process();
        dpad_down.process();
        dpad_left.process();
        dpad_right.process();
        left_bumper.process();
        right_bumper.process();
        cross.process();
        square.process();
        circle.process();
        triangle.process();
        left_trigger.process();
        right_trigger.process();
        left_stick.process();
        right_stick.process();
    }
}

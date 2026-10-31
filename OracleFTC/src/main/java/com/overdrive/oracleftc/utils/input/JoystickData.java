package com.overdrive.oracleftc.utils.input;

/***
 * Class to represent data from a {@link OracleGamepad}'s joystick
 */
public class JoystickData {
    public final double x, y;
    public final boolean pressed;

    public JoystickData(double x, double y, boolean pressed) {
        this.x = x;
        this.y = y;
        this.pressed = pressed;
    }
}

package org.firstinspires.ftc.teamcode.utils;

import com.pedropathing.math.Vector2D;

public final class FieldConstants {
    public static final double FIELD_HALF_Y = 70.75;
    public static final Vector2D HIVE_RIGHT = Vector2D.cartesian(57.7, 54.9);
    public static final Vector2D HIVE_LEFT  = Vector2D.cartesian(57.7 + 1, 141.5 - 3 - 54.9);

    private FieldConstants() {}

    public static Vector2D hiveFor(double robotY) {
        return robotY > FIELD_HALF_Y ? HIVE_LEFT : HIVE_RIGHT;
    }
}

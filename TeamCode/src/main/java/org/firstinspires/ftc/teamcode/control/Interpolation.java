package org.firstinspires.ftc.teamcode.control;

import com.pedropathing.math.Vector2D;

public final class Interpolation {
    private static final Vector2D HIVE_RIGHT = Vector2D.cartesian(57.7, 54.9);
    private static final Vector2D HIVE_LEFT = Vector2D.cartesian(57.7 + 1, 141.5 - 3 - 54.9);

    // Tabele ordonate crescator după distanță (în inci)
    // { distanta_inci, valoare }
    private static final double[][] AIR_TIME_TABLE = {
            {24.0, 0.30},
            {48.0, 0.45},
            {72.0, 0.60},
            {96.0, 0.75}
    };

    private static final double[][] FLYWHEEL_TABLE = {
            {24.0, 1100.0},
            {48.0, 1400.0},
            {72.0, 1720.0},
            {96.0, 2050.0}
    };

    private Interpolation() {
    }

    public static double getAirTime(double x, double y) {
        double distance = distanceToHive(x, y);
        return lerpTable(AIR_TIME_TABLE, distance);
    }

    public static double getFlywheelVelocity(double x, double y) {
        double distance = distanceToHive(x, y);
        return lerpTable(FLYWHEEL_TABLE, distance);
    }

    private static double distanceToHive(double x, double y) {
        Vector2D hive = y > 70.75 ? HIVE_LEFT : HIVE_RIGHT;
        double dx = hive.x() - x;
        double dy = hive.y() - y;
        return Math.hypot(dx, dy);
    }

    private static double lerpTable(double[][] table, double x) {
        if (x <= table[0][0]) {
            return table[0][1];
        }
        int last = table.length - 1;
        if (x >= table[last][0]) {
            return table[last][1];
        }

        for (int i = 0; i < last; i++) {
            double x0 = table[i][0];
            double y0 = table[i][1];
            double x1 = table[i + 1][0];
            double y1 = table[i + 1][1];

            if (x >= x0 && x <= x1) {
                double t = (x - x0) / (x1 - x0);
                return y0 + t * (y1 - y0);
            }
        }

        return table[last][1];
    }
}

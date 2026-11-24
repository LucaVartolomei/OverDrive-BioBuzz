package org.firstinspires.ftc.teamcode.control;

//import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.math.Velocity;

import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.utils.FieldConstants;

//@Config("Shooting Controller")
public final class ShootingController {
    private static final int SOTM_ITERATIONS = 4;
    private static final double MIN_SOTM_SPEED = 1.0; // in/s

    public static double turretOffsetX = 0.0;   // inches, fill in from CAD
    public static double turretOffsetY = 0.0;

    public static double turningGain = 0.0; // = latencySeconds * 180 / Math.PI

    private final ShooterSubsystem shooterSubsystem;

    public ShootingController(ShooterSubsystem shooterSubsystem) {
        this.shooterSubsystem = shooterSubsystem;
    }

    public void prepareForLocation(Pose pose) {
        prepareForLocationSotm(pose, Velocity.zero());
    }

    public void prepareForLocationSotm(Pose pose, Velocity velocity) {
        double c = Math.cos(pose.heading()), s = Math.sin(pose.heading());
        double rx = turretOffsetX * c - turretOffsetY * s;
        double ry = turretOffsetX * s + turretOffsetY * c;
        Vector2D turretPos = Vector2D.cartesian(pose.x() + rx, pose.y() + ry);

        Vector2D hive = FieldConstants.hiveFor(pose.y());
        Vector2D virtual = virtualRobot(turretPos, velocity.toVector2D(), hive);

        shooterSubsystem.setShooterTarget(Interpolation.getFlywheelVelocity(virtual, hive));
        double relativeDeg = Math.toDegrees(hive.minus(virtual).theta())
                - Math.toDegrees(pose.heading());
        shooterSubsystem.setTurretAngle(relativeDeg);
    }

    private static Vector2D virtualRobot(Vector2D robot, Vector2D vel, Vector2D hive) {
        if (vel.magnitude() < MIN_SOTM_SPEED) return robot;

        Vector2D virtual = robot;
        for (int i = 0; i < SOTM_ITERATIONS; i++) {
            double airTime = Interpolation.getAirTime(virtual, hive);
            virtual = robot.plus(vel.times(airTime));
        }
        return virtual;
    }
}
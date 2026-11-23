package org.firstinspires.ftc.teamcode.control;

//import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.math.Velocity;

import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

//@Config("Shooting Controller")
public final class ShootingController {
    private static final double FIELD_HALF_Y = 70.75;
    private static final Vector2D HIVE_POSITION_RIGHT = Vector2D.cartesian(57.7, 54.9);
    private static final Vector2D HIVE_POSITION_LEFT = Vector2D.cartesian(57.7 + 1, 141.5 - 3 - 54.9);
    private static final int SOTM_ITERATIONS = 10;
    private static final double MIN_SOTM_SPEED = 1.0;

    public static double turningGain = 0.0;
    public static boolean useVectorAim = true;

    private final ShooterSubsystem shooterSubsystem;

    public ShootingController(ShooterSubsystem shooterSubsystem) {
        this.shooterSubsystem = shooterSubsystem;
    }

    // Tragere de pe loc (fara compensare de viteza)
    public void prepareForLocation(Pose pose) {
        prepareForLocation(pose, Velocity.zero());
    }

    // Pregatire standard pentru o pozitie si viteza data
    public void prepareForLocation(Pose pose, Velocity velocity) {
        spinUp(pose);
        aim(pose, velocity);
    }

    // Tragere din mers (SOTM) - alege automat intre modul virtualPose si modul vectorial
    public void prepareForLocationSotm(Pose pose, Velocity velocity) {
        if (useVectorAim) {
            prepareForLocationVector(pose, velocity);
        } else {
            prepareForLocationVirtualPose(pose, velocity);
        }
    }

    // MODUL 1: SOTM folosind virtualPose (pentru aim si spinUp)
    public void prepareForLocationVirtualPose(Pose pose, Velocity velocity) {
        Pose virtualPose = calculateVirtualRobot(pose.toVector2D(), velocity.toVector2D())
                .toPose(pose.heading());
        spinUp(virtualPose);
        aim(virtualPose, velocity);
    }

    // MODUL 2: SOTM folosind formula vectoriala in aim()
    public void prepareForLocationVector(Pose pose, Velocity velocity) {
        Vector2D virtualPos = calculateVirtualRobot(pose.toVector2D(), velocity.toVector2D());
        // Poti folosi virtualPos pentru flywheel (distanta estimata) sau pose.toVector2D() pentru pozitia curenta
        spinUp(virtualPos.toPose(pose.heading()));
        aimVectorial(pose, velocity);
    }

    public void spinUp(Pose pose) {
        double targetVelocity = Interpolation.getFlywheelVelocity(pose.x(), pose.y());
        shooterSubsystem.setShooterPower(targetVelocity);
    }

    // aim() cand primeste deja pozitia tinta (de ex. virtualPose sau pose de pe loc)
    public void aim(Pose pose, Velocity velocity) {
        Vector2D hivePosition = getTargetHive(pose.y());
        double turretAngle = Math.toDegrees(hivePosition.minus(pose.toVector2D()).theta());
        applyTurretTarget(turretAngle, pose.heading(), velocity.omega);
    }

    // aim() bazat pe formula vectoriala: (p_hive - p_robot) - v_robot * airTime
    public void aimVectorial(Pose pose, Velocity velocity) {
        Vector2D robotPos = pose.toVector2D();
        Vector2D robotVel = velocity.toVector2D();
        Vector2D hivePosition = getTargetHive(robotPos.y());

        double airTime = Interpolation.getAirTime(robotPos.x(), robotPos.y());
        Vector2D aimVector = hivePosition
                .minus(robotPos)
                .minus(robotVel.times(airTime));

        double turretAngle = Math.toDegrees(aimVector.theta());
        applyTurretTarget(turretAngle, pose.heading(), velocity.omega);
    }

    private static Vector2D calculateVirtualRobot(Vector2D robotPose, Vector2D robotVelocity) {
        if (robotVelocity.magnitude() < MIN_SOTM_SPEED) {
            return robotPose;
        }

        Vector2D virtualPose = robotPose;
        for (int i = 0; i < SOTM_ITERATIONS; i++) {
            double airTime = Interpolation.getAirTime(virtualPose.x(), virtualPose.y());
            virtualPose = robotPose.plus(robotVelocity.times(airTime));
        }
        return virtualPose;
    }

    private static Vector2D getTargetHive(double y) {
        return y > FIELD_HALF_Y ? HIVE_POSITION_LEFT : HIVE_POSITION_RIGHT;
    }

    private void applyTurretTarget(double fieldTargetAngleDeg, double robotHeadingRad, double omega) {
        double relativeTurretAngleDeg = fieldTargetAngleDeg
                - Math.toDegrees(robotHeadingRad)
                - turningGain * omega;
        shooterSubsystem.setTurretAngle(relativeTurretAngleDeg);
    }
}
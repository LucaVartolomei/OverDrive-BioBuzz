package com.overdrive.oracleftc.hardware.subsystem;

import com.overdrive.oracleftc.commands.Command;
import com.overdrive.oracleftc.commands.InstantCommand;
import com.overdrive.oracleftc.hardware.OracleLynxVoltageSensor;
import com.overdrive.oracleftc.math.control.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.concurrent.atomic.AtomicReference;

public class FlywheelActuator {
    private final Subsystem subsystem;
    private final String name;
    private final DcMotorEx[] motors;
    private final OracleLynxVoltageSensor voltageSensor;

    public PIDController pid;
    public double kF;
    public double tolerance;
    public double maxVelocity;

    private final AtomicReference<Double> targetVelocity = new AtomicReference<>(0.0);
    private boolean enabled = true;

    public FlywheelActuator(
            Subsystem subsystem,
            String name,
            PIDController pid,
            double kF,
            double tolerance,
            double maxVelocity,
            DcMotorEx... motors
    ) {
        this.subsystem = subsystem;
        this.name = name;
        this.pid = pid;
        this.kF = kF;
        this.tolerance = tolerance;
        this.maxVelocity = maxVelocity;
        this.motors = motors;
        this.voltageSensor = subsystem.hardwareMap.getAll(OracleLynxVoltageSensor.class).iterator().next();

        for (DcMotorEx motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }

    public boolean setTarget(double targetTicksPerSec) {
        double clamped = Math.max(0.0, Math.min(maxVelocity, targetTicksPerSec));
        targetVelocity.set(clamped);
        return true;
    }

    public double getTarget() {
        return targetVelocity.get();
    }

    public double getVelocity() {
        if (motors.length == 0) {
            return 0.0;
        }
        return motors[0].getVelocity();
    }

    public boolean isAtTarget() {
        double target = getTarget();
        return target > 1.0 && Math.abs(target - getVelocity()) <= tolerance;
    }

    public Command enable() {
        return new InstantCommand(() -> enabled = true);
    }

    public Command disable() {
        return new InstantCommand(() -> {
            enabled = false;
            setAllMotorsPower(0.0);
        });
    }

    public Command reset() {
        return new InstantCommand(() -> {
            targetVelocity.set(0.0);
            setAllMotorsPower(0.0);
        });
    }

    public Command update() {
        return Command.builder()
                .update(() -> {
                    double target = getTarget();
                    double current = getVelocity();

                    if (!enabled || target <= 1.0) {
                        setAllMotorsPower(0.0);
                        addTelemetry(current, 0.0);
                        return;
                    }

                    double rawOutput = pid.update(target, current) + (kF * target);
                    double voltageScale = 12.0 / voltageSensor.getVoltage();
                    double finalPower = clamp(rawOutput * voltageScale, -1.0, 1.0);

                    setAllMotorsPower(finalPower);
                    addTelemetry(current, finalPower);
                })
                .build();
    }

    private void setAllMotorsPower(double power) {
        for (DcMotorEx motor : motors) {
            motor.setPower(power);
        }
    }

    private void addTelemetry(double currentVelocity, double power) {
        subsystem.telemetry.addData(String.format("%s.velocity", name), currentVelocity);
        subsystem.telemetry.addData(String.format("%s.target", name), getTarget());
        subsystem.telemetry.addData(String.format("%s.power", name), power);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
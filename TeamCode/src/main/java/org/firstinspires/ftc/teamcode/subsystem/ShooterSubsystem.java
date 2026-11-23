package org.firstinspires.ftc.teamcode.subsystem;

import com.overdrive.oracleftc.commands.Command;
import com.overdrive.oracleftc.commands.InstantCommand;
import com.overdrive.oracleftc.hardware.OracleLynxVoltageSensor;
import com.overdrive.oracleftc.hardware.subsystem.FlywheelActuator;
import com.overdrive.oracleftc.hardware.subsystem.ServoActuator;
import com.overdrive.oracleftc.hardware.subsystem.Subsystem;
import com.overdrive.oracleftc.math.control.PIDController;
import com.overdrive.oracleftc.math.control.TrapezoidalMotionProfile;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class ShooterSubsystem extends Subsystem {

    private final ServoImplEx turretServo;
    private final ServoActuator turret;

    private final DcMotorEx shooterMotor;
    private final FlywheelActuator shooter;

    private final OracleLynxVoltageSensor voltageSensor;

    public ShooterSubsystem(OpMode opMode) {
        super(opMode);

        turretServo = hardwareMap.get(ServoImplEx.class, "turret");
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");

        voltageSensor = (OracleLynxVoltageSensor) hardwareMap.voltageSensor.iterator().next();

        turret = new ServoActuator(this, "turret", new TrapezoidalMotionProfile(0,0,0), turretServo) {
            @Override
            public Command reset() {
                this.setTarget(0.5);
                return new InstantCommand(()->turretServo.setPosition(0.5));
            }

            @Override
            public boolean setTarget(double target) {
                target = Math.max(-90, Math.min(target, 90));
                target = (target / 355.0) + 0.5; // replace 0.5 with servo position while standing straight
                this.target.set(target);
                return true;
            }
        };

        shooter = new FlywheelActuator(this, "shooter", new PIDController(0,0,0), 0, 80, 3000, shooterMotor){
            @Override
            public Command reset() {
                this.setTarget(0);
                return new InstantCommand(()->shooterMotor.setVelocity(0));
            }
        };

    }

    public void setShooterPower(double power){
        double voltage = voltageSensor.getVoltage();

        shooterMotor.setPower((12.0 / voltage) * power);
    }

    public void setShooterTarget(double velocity){
        shooter.setTarget(velocity);
    }

    public void setTurretAngle(double degrees){
        turret.setTarget(degrees);
    }
}

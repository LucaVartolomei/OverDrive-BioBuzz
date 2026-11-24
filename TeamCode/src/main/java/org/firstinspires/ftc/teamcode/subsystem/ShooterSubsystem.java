package org.firstinspires.ftc.teamcode.subsystem;

import com.overdrive.oracleftc.commands.Command;
import com.overdrive.oracleftc.commands.InstantCommand;
import com.overdrive.oracleftc.hardware.OracleLynxVoltageSensor;
import com.overdrive.oracleftc.hardware.subsystem.FlywheelActuator;
import com.overdrive.oracleftc.hardware.subsystem.ServoActuator;
import com.overdrive.oracleftc.hardware.subsystem.Subsystem;
import com.overdrive.oracleftc.math.control.PIDController;
import com.overdrive.oracleftc.math.control.TrapezoidalMotionProfile;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.control.ShootingController;

public class ShooterSubsystem extends Subsystem {

    private final ServoImplEx turretServo;
    private final ServoActuator turret;

    private final DcMotorEx shooterMotor;
    private final FlywheelActuator shooter;

    private final ShootingController shootingController;

    private final GoBildaPinpointDriver pinpoint;

    private final OracleLynxVoltageSensor voltageSensor;

    private static final double TURRET_RANGE_DEG = 355.0;  // servo 0..1 = this many turret degrees
    private static final double TURRET_LIMIT_DEG = 175.0;  // keep below 177.5 to protect cables
    private static final double TURRET_CENTER_POS = 0.5;   // tune: servo position when facing forward

    private double lastTurretDeg = 0;

    public ShooterSubsystem(OpMode opMode) {
        super(opMode);

        turretServo = hardwareMap.get(ServoImplEx.class, "turret");
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        voltageSensor = (OracleLynxVoltageSensor) hardwareMap.voltageSensor.iterator().next();

        turret = new ServoActuator(this, "turret", new TrapezoidalMotionProfile(0,0,0), turretServo) {
            @Override
            public Command reset() {
                setTarget(0);
                return new InstantCommand(() -> turretServo.setPosition(TURRET_CENTER_POS));
            }

            @Override
            public boolean setTarget(double deg) {
                deg = Math.max(-TURRET_LIMIT_DEG, Math.min(TURRET_LIMIT_DEG, deg));
                this.target.set(deg / TURRET_RANGE_DEG + TURRET_CENTER_POS);
                return true;
            }
        };

        shooter = new FlywheelActuator(this, "shooter", new PIDController(0,0,0), 0, 80, 3000, shooterMotor){
            @Override
            public Command reset() {
                return new InstantCommand(() -> {
                    setTarget(0);
                    shooterMotor.setPower(0);
                });
            }
        };

        shootingController = new ShootingController(this);

    }

    public void setShooterTarget(double velocity){
        shooter.setTarget(velocity);
    }

    public void setTurretAngle(double degrees) {
        double wrapped = ((degrees + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;

        if (Math.abs(wrapped) > TURRET_LIMIT_DEG) {
            wrapped = Math.copySign(TURRET_LIMIT_DEG, lastTurretDeg == 0 ? wrapped : lastTurretDeg);
        }
        lastTurretDeg = wrapped;
        turret.setTarget(wrapped);
    }

    public Command updateTurret(){
        return Command.builder()
                .update(()->{

                })
                .build();
    }
}

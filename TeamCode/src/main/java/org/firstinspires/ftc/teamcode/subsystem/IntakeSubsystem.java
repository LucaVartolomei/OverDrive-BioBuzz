package org.firstinspires.ftc.teamcode.subsystem;

import com.overdrive.oracleftc.commands.Command;
import com.overdrive.oracleftc.commands.InstantCommand;
import com.overdrive.oracleftc.commands.SequentialCommand;
import com.overdrive.oracleftc.hardware.OracleLynxVoltageSensor;
import com.overdrive.oracleftc.hardware.subsystem.ServoActuator;
import com.overdrive.oracleftc.hardware.subsystem.Subsystem;
import com.overdrive.oracleftc.math.control.TrapezoidalMotionProfile;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class IntakeSubsystem extends Subsystem {

    private final DcMotorEx intake, feeder;
    private final ServoImplEx pitchLeftServo, pitchRightServo;
    private final CRServoImplEx intakeLeft, intakeRight;

    private final OracleLynxVoltageSensor voltageSensor;

    public final ServoActuator pitchLeft;
    public final ServoActuator pitchRight;

    public IntakeSubsystem(OpMode opMode){
        super(opMode);

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        feeder = hardwareMap.get(DcMotorEx.class, "feeder");

        pitchLeftServo = hardwareMap.get(ServoImplEx.class, "pitchLeft");
        pitchRightServo = hardwareMap.get(ServoImplEx.class, "pitchRight");

        intakeLeft = hardwareMap.get(CRServoImplEx.class, "intakeLeft");
        intakeRight = hardwareMap.get(CRServoImplEx.class, "intakeRight");

        //to do: reverse intakeLeft/intakeRight !!!

        pitchLeft = new ServoActuator(this, "pitchLeft", new TrapezoidalMotionProfile(0,0,0), pitchLeftServo) {

            private static final double GEAR_RATIO = 2;
            private static final double OFFSET = 0.5;

            //to do: find OFFSET!!!

            @Override
            public Command reset() {
                target.set(OFFSET);
                return new InstantCommand(()-> pitchLeftServo.setPosition(OFFSET));
            }

            @Override
            public boolean setTarget(double target) {
                target = (target * GEAR_RATIO) / 324.0 + OFFSET;
                if(target > OFFSET + 0.2778) target = OFFSET + 0.2778;
                if(target < OFFSET - 0.2778) target = OFFSET + 0.2778;
                this.target.set(target);
                return true;
            }
        };

        pitchRight = new ServoActuator(this, "pitchRight", new TrapezoidalMotionProfile(0,0,0), pitchRightServo) {

            private static final double GEAR_RATIO = 2;
            private static final double OFFSET = 0.5;

            //to do: find OFFSET!!!

            @Override
            public Command reset() {
                target.set(OFFSET);
                return new InstantCommand(()-> pitchRightServo.setPosition(OFFSET));
            }

            @Override
            public boolean setTarget(double target) {
                target = (target * GEAR_RATIO) / 324.0 + OFFSET;
                if(target > OFFSET + 0.2778) target = OFFSET + 0.2778;
                if(target < OFFSET - 0.2778) target = OFFSET + 0.2778;
                this.target.set(target);
                return true;
            }
        };

        voltageSensor = (OracleLynxVoltageSensor) hardwareMap.voltageSensor.iterator().next();
    }

    public void setIntakePower(double intakePower){
        double voltage = voltageSensor.getVoltage();

        intake.setPower((12.0 / voltage) * intakePower);
    }

    public void setFeederPower(double feederPower){
        double voltage = voltageSensor.getVoltage();

        feeder.setPower((12.0 / voltage) * feederPower);
    }

    public void setPitchAngle(double degrees){
        pitchLeft.setTarget(degrees);
        pitchRight.setTarget(-degrees);//either reversed
    }

    public Command intake(){
        return new InstantCommand(()->{
            setIntakePower(1);
            intakeLeft.setPower(1);
            intakeRight.setPower(1);
        });
    }

    public Command outtake(){
        return new InstantCommand(()->{
            setIntakePower(-1);
            intakeLeft.setPower(-1);
            intakeRight.setPower(-1);
        });
    }

    public Command stopIntake(){
        return new InstantCommand(()->{
            setIntakePower(0);
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
        });
    }

    public Command feedShooter(){
        return new InstantCommand(()->{
            setFeederPower(1);
        });
    }

    public Command stopRollers(){
        return new InstantCommand(()->{
            setFeederPower(0);
        });
    }

    public Command pitchDown(){
        return new InstantCommand(()->setPitchAngle(-30));
    }

    public Command pitchUp(){
        return new InstantCommand(()->setPitchAngle(10));
    }

    public Command resetPitch(){
        return new InstantCommand(()->setPitchAngle(0));
    }

    public Command reset(){
        return new SequentialCommand(
                resetPitch(),
                stopIntake(),
                stopRollers()
        );
    }

}

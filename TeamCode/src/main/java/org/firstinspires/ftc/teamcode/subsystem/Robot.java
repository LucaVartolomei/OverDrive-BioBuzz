package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Robot {
    public final MecanumSubsystem drive;

    public Robot(OpMode opMode){
        this.drive = new MecanumSubsystem(opMode);
    }
}

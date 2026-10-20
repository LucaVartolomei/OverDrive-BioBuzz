package ExperimentalCode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name = "Road Runner Test", group = "Test")
public class RoadRunnerTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // Tests that SampleMecanumDrive and DriveConstants
        // are correctly connected to Road Runner.
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        Trajectory trajectory = drive.trajectoryBuilder(startPose)
                .forward(12)
                .build();

        telemetry.addLine("Road Runner initialized successfully.");
        telemetry.addData("Start pose", startPose);
        telemetry.addLine("Trajectory built successfully.");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        drive.followTrajectory(trajectory);

        telemetry.addLine("Trajectory completed.");
        telemetry.update();
    }
}
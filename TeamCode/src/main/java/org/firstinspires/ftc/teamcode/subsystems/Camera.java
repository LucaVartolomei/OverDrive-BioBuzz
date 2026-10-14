package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.limelightvision.LLStatus;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

/**
 * Camera subsystem: the Limelight 3A (issue #7, decisions 015, 016).
 *
 * SKELETON: it starts the camera and reports which AprilTags it sees. How we aim the turret with this
 * data is still being designed (issue #7).
 *
 * The camera is mounted ON THE TURRET at a fixed up/down angle, so it turns with the turret.
 * Only the turret moves based on camera data, never the whole robot.
 *
 * Setup before this works:
 *   1. Plug the Limelight into the Control Hub USB port labeled "USB 3.0". The USB 2.0 port will not work.
 *   2. Add it to the Driver Station config as "limelight".
 *   3. Set up an AprilTag pipeline in the Limelight's web page, in the slot set by APRILTAG_PIPELINE.
 */
public class Camera {

    // Config name (decision 009). Must match the Driver Station robot configuration.
    public static final String LIMELIGHT_NAME = "limelight";

    // TODO(#7): which Limelight pipeline slot (0-9) has our AprilTag pipeline.
    public static final int APRILTAG_PIPELINE = 0;

    private final Limelight3A limelight;

    public Camera(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, LIMELIGHT_NAME);

        // Start polling connection first, then switch to the AprilTag pipeline slot.
        limelight.start();
        limelight.pipelineSwitch(APRILTAG_PIPELINE);
    }

    /** True if the Limelight sees at least one AprilTag right now. */
    public boolean seesAprilTag() {
        return !getVisibleTagIds().isEmpty();
    }

    /** The ID numbers of every AprilTag the Limelight sees right now (BIOBUZZ uses IDs 30-45). */
    public List<Integer> getVisibleTagIds() {
        return getVisibleTagIds(limelight.getLatestResult());
    }

    /** Helper method using an existing LLResult packet to avoid double-polling the SDK. */
    public List<Integer> getVisibleTagIds(LLResult result) {
        List<Integer> ids = new ArrayList<>();
        if (result != null) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null) {
                for (LLResultTypes.FiducialResult tag : fiducials) {
                    ids.add(tag.getFiducialId());
                }
            }
        }
        return ids;
    }

    /** Stop reading results from the Limelight. Call at the end of an OpMode. */
    public void stop() {
        limelight.stop();
    }

    /** Show clean connection and detection details on the Driver Station. */
    public void addTelemetry(Telemetry telemetry) {
        boolean connected = limelight.isConnected();
        telemetry.addData("Camera connected", connected);

        if (!connected) {
            telemetry.addData("Camera status", "Disconnected - Check USB 3.0 port");
            return;
        }

        LLStatus status = limelight.getStatus();
        if (status != null) {
            telemetry.addData("FPS", status.getFps());
        }

        LLResult result = limelight.getLatestResult();
        List<Integer> visibleIds = getVisibleTagIds(result);

        if (visibleIds.isEmpty()) {
            telemetry.addData("AprilTags seen", "None");
        } else {
            telemetry.addData("AprilTags seen", visibleIds);
        }
    }
}

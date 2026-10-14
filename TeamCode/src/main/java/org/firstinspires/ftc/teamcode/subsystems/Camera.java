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
 * Camera subsystem: the Limelight 3A (issue #7, decisions 009, 015, 016, 019).
 *
 * The camera is mounted ON THE TURRET at an upward tilt angle so it can see the
 * BIOBUZZ CELL AprilTag clusters mounted under the HIVE (TU03 Section 9.9).
 *
 * Hardware config name: "limelight" (decision 009). This has been set also in the REV Control Hub for the IronDogs configuration
 * Must be plugged into the Control Hub USB 3.0 port or will not work.
 *
 * BIOBUZZ CELL AprilTag IDs (TU03 Fig 9-17):
 *   - Red, far side:        30, 31, 32, 33
 *   - Red, audience side:   34, 35, 36, 37
 *   - Blue, audience side:  38, 39, 40, 41
 *   - Blue, far side:       42, 43, 44, 45
 */
public class Camera {

    // Config name (decision 009). Must match the Driver Station robot configuration.
    public static final String LIMELIGHT_NAME = "limelight";

    // Which Limelight pipeline slot (0-9) has our AprilTag pipeline.
    public static final int APRILTAG_PIPELINE = 0;

    // TODO(#7): Physical upward tilt angle of the camera mount (in degrees).
    // The HIVE cells are ~44 in above the floor with AprilTags facing DOWN, so the camera tilts UP.
    // STUDENTS: Update this value once the physical mount angle is measured or decided!
    public static final double CAMERA_MOUNT_TILT_DEGREES = 0.0; // <-- EDIT THIS VALUE (e.g. 25.0)

    // BIOBUZZ AprilTag ID Ranges Template (may change)
    public static final int TAG_RED_FAR_MIN       = 30;
    public static final int TAG_RED_FAR_MAX       = 33;
    public static final int TAG_RED_AUDIENCE_MIN  = 34;
    public static final int TAG_RED_AUDIENCE_MAX  = 37;
    public static final int TAG_BLUE_AUDIENCE_MIN = 38;
    public static final int TAG_BLUE_AUDIENCE_MAX = 41;
    public static final int TAG_BLUE_FAR_MIN      = 42;
    public static final int TAG_BLUE_FAR_MAX      = 45;

    private final Limelight3A limelight;

    public Camera(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, LIMELIGHT_NAME);

        // Start polling connection first, then switch to the AprilTag pipeline slot.
        limelight.start();
        limelight.pipelineSwitch(APRILTAG_PIPELINE);
    }

    /** True if the Limelight sees a valid target right now. */
    public boolean hasTarget() {
        LLResult result = limelight.getLatestResult();
        return result != null && result.isValid();
    }

    /** True if the Limelight sees at least one AprilTag right now. */
    public boolean seesAprilTag() {
        return !getVisibleTagIds().isEmpty();
    }

    /**
     * Horizontal angle offset to the target in degrees (+ = target is to the LEFT, - = RIGHT).
     * Flips Limelight's tx (+ = right) to match our team convention (+ = left, decision 007).
     * Can be added directly to Turret getAngle() or fed to Turret aiming.
     */
    public double getTargetDegreesLeft() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return -result.getTx();
        }
        return 0.0;
    }

    /** Vertical angle offset to the target in degrees (+ = above crosshair, - = below). */
    public double getTargetDegreesUp() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return result.getTy();
        }
        return 0.0;
    }

    /** Target area as a percentage of the image (0 to 100). */
    public double getTargetArea() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return result.getTa();
        }
        return 0.0;
    }

    /** The ID numbers of every AprilTag the Limelight sees right now (BIOBUZZ uses IDs 30-45). */
    public List<Integer> getVisibleTagIds() {
        return getVisibleTagIds(limelight.getLatestResult());
    }

    /** Helper method using an existing LLResult packet to avoid double-polling the SDK. */
    public List<Integer> getVisibleTagIds(LLResult result) {
        List<Integer> ids = new ArrayList<>();
        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null) {
                for (LLResultTypes.FiducialResult tag : fiducials) {
                    ids.add(tag.getFiducialId());
                }
            }
        }
        return ids;
    }

    /** Check if a given AprilTag ID belongs to a BIOBUZZ CELL target (IDs 30-45). If not ignore (overkill but) */
    public static boolean isBiobuzzTag(int tagId) {
        return tagId >= TAG_RED_FAR_MIN && tagId <= TAG_BLUE_FAR_MAX;
    }

    /** Check if a tag ID belongs to Red Alliance CELLs (IDs 30-37). */
    public static boolean isRedAllianceTag(int tagId) {
        return tagId >= TAG_RED_FAR_MIN && tagId <= TAG_RED_AUDIENCE_MAX;
    }

    /** Check if a tag ID belongs to Blue Alliance CELLs (IDs 38-45). */
    public static boolean isBlueAllianceTag(int tagId) {
        return tagId >= TAG_BLUE_AUDIENCE_MIN && tagId <= TAG_BLUE_FAR_MAX;
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

        telemetry.addData("Camera mount tilt (°)", "%.1f", CAMERA_MOUNT_TILT_DEGREES);

        if (visibleIds.isEmpty()) {
            telemetry.addData("AprilTags seen", "None");
        } else {
            telemetry.addData("AprilTags seen", visibleIds);
            telemetry.addData("Target angle left (°)", "%.1f", getTargetDegreesLeft());
            telemetry.addData("Target angle up (°)", "%.1f", getTargetDegreesUp());
            telemetry.addData("Target area (%)", "%.2f", getTargetArea());
        }
    }
}

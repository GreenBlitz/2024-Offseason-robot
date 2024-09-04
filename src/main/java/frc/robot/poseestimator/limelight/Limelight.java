package frc.robot.poseestimator.limelight;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import frc.utils.DriverStationUtils;
import frc.utils.GBSubsystem;

import java.util.Optional;

public class Limelight extends GBSubsystem {

    private NetworkTableEntry robotPoseEntry, idEntry, tagPoseEntry;

    private String name;

    public Limelight(String limelightName) {
        super(VisionConstants.LIMELIGHT_LOGPATH_PREFIX + limelightName + "/");
        this.name = limelightName;
        robotPoseEntry = NetworkTableInstance.getDefault().getTable(name).getEntry("botpose_wpiblue");
        tagPoseEntry = NetworkTableInstance.getDefault().getTable(name).getEntry("targetpose_cameraspace");
        idEntry = NetworkTableInstance.getDefault().getTable(name).getEntry("tid");
    }

    public Optional<Pair<Pose2d, Double>> getUpdatedPose2DEstimation() {
        int id = (int) idEntry.getInteger(-1);
        if (id == -1) {
            return Optional.empty();
        }

        double[] poseArray = robotPoseEntry.getDoubleArray(new double[VisionConstants.LIMELIGHT_ENTRY_ARRAY_LENGTH]);
        double processingLatencySeconds = poseArray[VisionConstants.getValue(VisionConstants.LIMELIGHT_ARRAY_VALUE.TOTAL_LATENCY)] / 1000;
        double timestamp = Timer.getFPGATimestamp() - processingLatencySeconds;

        Rotation2d angleOffset = DriverStationUtils.isBlueAlliance() ? VisionConstants.BLUE_ALLIANCE_POSE_OFFSET : VisionConstants.RED_ALLIANCE_POSE_OFFSET;

        Pose2d robotPose = new Pose2d(
                poseArray[VisionConstants.getValue(VisionConstants.LIMELIGHT_ARRAY_VALUE.X_AXIS)],
                poseArray[VisionConstants.getValue(VisionConstants.LIMELIGHT_ARRAY_VALUE.Y_AXIS)],
                Rotation2d.fromDegrees(poseArray[VisionConstants.getValue(VisionConstants.LIMELIGHT_ARRAY_VALUE.PITCH_ANGLE)] - angleOffset.getDegrees())
        );

        return Optional.of(new Pair<>(robotPose, timestamp));
    }

    public double getAprilTagHeight() {
        double[] poseArray = tagPoseEntry.getDoubleArray(new double[VisionConstants.LIMELIGHT_ENTRY_ARRAY_LENGTH]);
        return poseArray[VisionConstants.getValue(VisionConstants.LIMELIGHT_ARRAY_VALUE.Y_AXIS)];
    }

    public boolean isAprilTagInProperHeight() {
        boolean aprilTagHeightConfidence = Math.abs(getAprilTagHeight() - VisionConstants.APRIL_TAG_HEIGHT_METERS) < VisionConstants.APRIL_TAG_HEIGHT_TOLERANCE_METERS;
        return aprilTagHeightConfidence;
    }

    public double getDistanceFromAprilTag() {
        double[] poseArray = tagPoseEntry.getDoubleArray(new double[VisionConstants.LIMELIGHT_ENTRY_ARRAY_LENGTH]);
        return poseArray[VisionConstants.getValue(VisionConstants.LIMELIGHT_ARRAY_VALUE.Z_AXIS)];
    }

    public boolean hasTarget() {
        return getUpdatedPose2DEstimation().isPresent();
    }

    @Override
    protected void subsystemPeriodic() {

    }

}

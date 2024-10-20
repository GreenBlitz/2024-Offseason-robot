package frc.robot.AutoPositioning;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.Field;
import frc.robot.poseestimator.GBPoseEstimator;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.subsystems.swerve.SwerveMath;
import frc.utils.shootinghelpers.ShootingHelpers;

public class AutoPositioning {

	private final Swerve swerve;
	private final GBPoseEstimator poseEstimator;

	public AutoPositioning(Swerve swerve, GBPoseEstimator poseEstimator) {
		this.swerve = swerve;
		this.poseEstimator = poseEstimator;
	}

	// @ formatter:off
	private Command driveToPose(Pose2d pose) {
		return swerve.getCommandsBuilder().driveToPose(poseEstimator::getEstimatedPose, () -> pose, this::isAtPose);
	}

	public Command goToClosestShootingPosition() {
		Translation2d closestPoint = ShootingHelpers.getClosestShootingPoint(poseEstimator.getEstimatedPose());
		Pose2d targetPose = new Pose2d(
			closestPoint.getX(),
			closestPoint.getY(),
			SwerveMath.getRelativeTranslation(poseEstimator.getEstimatedPose().getTranslation(), Field.getSpeaker().toTranslation2d()).getAngle()
		);
		return driveToPose(targetPose);
	}
	// @ formatter:on

	private boolean isAtPose(Pose2d pose) {
		Pose2d estimatedPose = poseEstimator.getEstimatedPose();
		return pose.minus(estimatedPose).getTranslation().getNorm() <= AutoPositioningConstants.IS_AT_POSE_TOLERANCE
			&& swerve.isAtHeading(estimatedPose.getRotation());
	}

	public Command stop() {
		return driveToPose(poseEstimator.getEstimatedPose());
	}

}

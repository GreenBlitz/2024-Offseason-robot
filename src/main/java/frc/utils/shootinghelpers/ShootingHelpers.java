package frc.utils.shootinghelpers;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.constants.Field;

public class ShootingHelpers {

	public static Translation2d getClosestShootingPoint(Pose2d robotPose) {
		return getClosestShootingPoint(
			robotPose.getTranslation(),
			ShootingHelpersConstants.SHOOTING_DISTANCE_FROM_SPEAKER,
			new Pair<>(
				ShootingHelpersConstants.SPEAKER_LOWER_BOUND_WITHIN_SHOOTING_RANGE,
				ShootingHelpersConstants.SPEAKER_UPPER_BOUND_WITHIN_SHOOTING_RANGE
			)
		);
	}

	@SafeVarargs
	public static Translation2d getClosestShootingPoint(
		Translation2d robotPosition,
		double DistanceForShootingFromSpeaker,
		Pair<Rotation2d, Rotation2d>... invalidRanges
	) {
		Translation2d speaker = Field.getSpeaker().toTranslation2d();
		Translation2d robotRelativeToSpeaker = robotPosition.minus(speaker);
		Rotation2d angleFromSpeaker = findAngleFromSpeaker(robotPosition);
		double angleFromSpeakerRadians = angleFromSpeaker.getRadians();

		Translation2d closestValidPointRelativeToSpeaker;

		for (Pair<Rotation2d, Rotation2d> invalidRange : invalidRanges) {
			double minimumRangeRadians = invalidRange.getFirst().getRadians();
			double maximumRangeRadians = invalidRange.getSecond().getRadians();

			if (angleFromSpeakerRadians >= minimumRangeRadians && angleFromSpeakerRadians <= maximumRangeRadians) {
				Rotation2d closestValidAngle = getClosestAngleIntervalBound(invalidRange.getFirst(), invalidRange.getSecond(), angleFromSpeaker);

				return angleToPoint(closestValidAngle, DistanceForShootingFromSpeaker).plus(speaker);
			}
		}

		closestValidPointRelativeToSpeaker = getCutPointOnRadiusFromCoordinates(robotRelativeToSpeaker, DistanceForShootingFromSpeaker);
		return closestValidPointRelativeToSpeaker.plus(speaker);
	}

	private static Translation2d angleToPoint(Rotation2d angle, double radius) {
		return new Translation2d(Math.cos(angle.getRadians()), Math.sin(angle.getRadians())).times(radius);
	}

	private static Rotation2d findAngleFromSpeaker(Translation2d robotPosition) {
		Translation2d speaker = Field.getSpeaker().toTranslation2d();
		Translation2d robotPoseRelativeToSpeaker = robotPosition.minus(speaker);
		return Rotation2d.fromRadians(Math.atan2(robotPoseRelativeToSpeaker.getY(), robotPoseRelativeToSpeaker.getX()));
	}

	private static Translation2d getCutPointOnRadiusFromCoordinates(Translation2d point, double radius) {
		double slope = point.getY() / point.getX();
		double closestValidPointX = radius / Math.sqrt(Math.pow(slope, 2) + 1);
		return new Translation2d(closestValidPointX, slope * closestValidPointX);
	}

	private static Rotation2d getClosestAngleIntervalBound(Rotation2d minimumRange, Rotation2d maximumRange, Rotation2d angleWithinRange) {
		if (Math.abs(angleWithinRange.getRadians() - minimumRange.getRadians()) <= (angleWithinRange.getRadians() - maximumRange.getRadians())) {
			return minimumRange;
		} else {
			return maximumRange;
		}
	}

}

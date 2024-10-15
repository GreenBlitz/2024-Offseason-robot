package frc.robot.subsystems.elevator;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.utils.Conversions;

public class ElevatorConstants {

	public static final String LOG_PATH = "Subsystems/Elevator/";

	public final static double GEAR_RATIO = 14.0 / 48.0;

	public static final double MINIMUM_ACHIEVABLE_POSITION_METERS = 0;

	public static final double MOTOR_ROTATIONS_TO_METERS_CONVERSION_RATIO =  (26.0 * 5.0 / Math.PI) * 0.001;

	public static final double REVERSE_SOFT_LIMIT_VALUE_METERS = 0;

	public static final double FORWARD_SOFT_LIMIT_VALUE_METERS = 0.22;

}

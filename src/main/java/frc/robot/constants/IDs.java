package frc.robot.constants;

import com.revrobotics.CANSparkLowLevel;
import edu.wpi.first.wpilibj.PowerDistribution;
import frc.robot.hardware.motor.sparkmax.SparkMaxDeviceID;
import frc.utils.battery.PowerDistributionDeviceID;

public class IDs {

	public static final PowerDistributionDeviceID POWER_DISTRIBUTION_DEVICE_ID = new PowerDistributionDeviceID(
		20,
		PowerDistribution.ModuleType.kRev
	);

	public static final SparkMaxDeviceID ELEVATOR_FIRST_MOTOR = new SparkMaxDeviceID(
			0,
			CANSparkLowLevel.MotorType.kBrushless
	);

	public static final SparkMaxDeviceID ELEVATOR_SECOND_MOTOR = new SparkMaxDeviceID(
			0,
			CANSparkLowLevel.MotorType.kBrushless
	);

}

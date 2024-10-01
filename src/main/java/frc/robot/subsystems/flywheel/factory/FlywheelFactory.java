package frc.robot.subsystems.flywheel.factory;

import frc.robot.Robot;
import frc.robot.subsystems.flywheel.FlywheelComponents;

public class FlywheelFactory {

	public static FlywheelComponents create(String logPath) {
		return switch (Robot.ROBOT_TYPE) {
			case REAL -> RealFlywheelConstants.generateFlywheelComponents(logPath, false);
			case SIMULATION -> null;
		};
	}

}

package frc.robot.subsystems.funnel;

import frc.robot.Robot;

public class FunnelFactory {

	public static FunnelStuff create(String logPath) {
		return switch (Robot.ROBOT_TYPE) {
			case REAL -> RealFunnelConstants.generateFunnelStuff(logPath);
			case SIMULATION -> null;
		};
	}

}

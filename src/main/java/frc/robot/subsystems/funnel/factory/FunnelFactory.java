package frc.robot.subsystems.funnel.factory;

import frc.robot.Robot;
import frc.robot.subsystems.funnel.FunnelAvatiach;

public class FunnelFactory {

	public static FunnelAvatiach create(String logPath) {
		return switch (Robot.ROBOT_TYPE) {
			case REAL -> RealFunnelConstants.generateFunnelStuff(logPath);
			case SIMULATION -> null;
		};
	}

}

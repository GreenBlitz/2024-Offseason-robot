package frc.robot.subsystems.chooser.factory;

import frc.robot.Robot;
import frc.robot.subsystems.chooser.ChooserStuff;

public class ChooserFactory {

	public static ChooserStuff create(String logPath) {
		return switch (Robot.ROBOT_TYPE) {
			case REAL -> RealChooserConstants.generateChooserStuff(logPath);
			case SIMULATION -> RealChooserConstants.generateChooserStuff(logPath);
		};
	}

}

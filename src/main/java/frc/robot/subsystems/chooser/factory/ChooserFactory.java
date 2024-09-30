package frc.robot.subsystems.chooser.factory;

import frc.robot.Robot;
import frc.robot.subsystems.chooser.ChooserAvatiach;

public class ChooserFactory {

    public static ChooserAvatiach create(String logPath) {
        return switch (Robot.ROBOT_TYPE) {
            case REAL -> RealChooserConstants.generateChooserStuff(logPath);
            case SIMULATION -> null;
        };
    }

}

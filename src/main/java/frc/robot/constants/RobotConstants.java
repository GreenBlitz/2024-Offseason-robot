package frc.robot.constants;

import edu.wpi.first.wpilibj.DriverStation;
import frc.utils.RobotTypeUtils;
import frc.utils.RobotTypeUtils.RobotType;

public class RobotConstants {

    public static final RobotType ROBOT_TYPE = RobotTypeUtils.determineRobotType(RobotType.REAL);

    public static final double DEFAULT_SIGNALS_FREQUENCY_HERTZ = 50;

    public static final DriverStation.Alliance SIMULATION_ALLIANCE = DriverStation.Alliance.Red;

    public static final boolean ENABLE_BATTERY_LIMITER = true;

}

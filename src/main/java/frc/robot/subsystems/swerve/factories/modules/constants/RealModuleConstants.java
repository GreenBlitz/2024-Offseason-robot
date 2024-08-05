package frc.robot.subsystems.swerve.factories.modules.constants;

import frc.robot.subsystems.swerve.SwerveName;
import frc.robot.subsystems.swerve.factories.swerveconstants.RealSwerveConstants;
import frc.robot.subsystems.swerve.modules.ModuleConstants;
import frc.robot.subsystems.swerve.modules.ModuleUtils;

public class RealModuleConstants {

    private static final double WHEEL_DIAMETER_METERS = 0.048359 * 2;
    private static final double COUPLING_RATIO = 0.59;

    protected static ModuleConstants getModuleConstants(SwerveName swerveName, ModuleUtils.ModuleType moduleType){
        return new ModuleConstants(
                swerveName.getLogPath(),
                moduleType.getModuleType(),
                WHEEL_DIAMETER_METERS,
                COUPLING_RATIO,
                RealSwerveConstants.VELOCITY_AT_12_VOLTS_METERS_PER_SECOND
        );
    }

}

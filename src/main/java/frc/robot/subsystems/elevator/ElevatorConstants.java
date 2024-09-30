package frc.robot.subsystems.elevator;

import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;

import java.util.function.Supplier;

public class ElevatorConstants {

    private final static double DEBOUNCE_TIME_PHYSICAL_LIMIT = 0.03;

    private final static double GEAR_RATIO = .3; //TODO: check this later

    public static ElevatorStuff generateElevatorStuff(String logPath) {
        SparkMaxWrapper mainSparkMaxWrapper = new SparkMaxWrapper(IDs.ELEVATOR_FIRST_MOTOR);
        SparkMaxWrapper secondarySparkMaxWrapper = new SparkMaxWrapper(IDs.ELEVATOR_SECOND_MOTOR);

        IMotor mainMotor  = new BrushlessSparkMAXMotor(
                logPath,
                mainSparkMaxWrapper,
                new SysIdRoutine.Config()
        );

        IMotor secondaryMotor  = new BrushlessSparkMAXMotor(
                logPath,
                secondarySparkMaxWrapper,
                new SysIdRoutine.Config()
        );

        Supplier<Double> position = () -> mainSparkMaxWrapper.getEncoder().getPosition();
        secondarySparkMaxWrapper.follow(mainSparkMaxWrapper);

        return new ElevatorStuff(
                logPath,
                logPath + "/digitalInputs",
                mainMotor,
                secondaryMotor,
        );
    }

}

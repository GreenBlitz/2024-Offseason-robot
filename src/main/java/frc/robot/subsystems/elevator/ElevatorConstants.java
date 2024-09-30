package frc.robot.subsystems.elevator;

import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.InputSignal;
import frc.robot.hardware.signal.cansparkmax.SparkMaxDoubleSignal;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ElevatorConstants {

    private final static double DEBOUNCE_TIME_PHYSICAL_LIMIT = 0.03;

    private final static double GEAR_RATIO = .3; //TODO: check this later
    
    protected final static double MAXIMUM_MOTORS_DELTA = 0.01;

    public static ElevatorStuff generateElevatorStuff(String logPath) {
        SparkMaxWrapper mainSparkMaxWrapper = new SparkMaxWrapper(IDs.ELEVATOR_FIRST_MOTOR);
        SparkMaxWrapper secondarySparkMaxWrapper = new SparkMaxWrapper(IDs.ELEVATOR_SECOND_MOTOR);

        ControllableMotor mainMotor  = new BrushlessSparkMAXMotor(
                logPath,
                mainSparkMaxWrapper,
                new SysIdRoutine.Config()
        );

        IMotor secondaryMotor  = new BrushlessSparkMAXMotor(
                logPath,
                secondarySparkMaxWrapper,
                new SysIdRoutine.Config()
        );

        Supplier<Double> mainMotorPosition = () -> mainSparkMaxWrapper.getEncoder().getPosition();
        SparkMaxDoubleSignal mainMotorPositionSignal = new SparkMaxDoubleSignal("motor position", mainMotorPosition);

        Supplier<Double> secondaryMotorPosition = () -> secondarySparkMaxWrapper.getEncoder().getPosition();
        SparkMaxDoubleSignal secondaryMotorPositionSignal = new SparkMaxDoubleSignal("motor position", secondaryMotorPosition);

        Supplier<Double> motorsVoltage = () -> (mainSparkMaxWrapper.getBusVoltage() * secondarySparkMaxWrapper.getAppliedOutput());
        SparkMaxDoubleSignal motorsVoltageSignal = new SparkMaxDoubleSignal("motor position", motorsVoltage);

        secondarySparkMaxWrapper.follow(mainSparkMaxWrapper);

        return new ElevatorStuff(
                logPath,
                logPath + "/digitalInputs",
                mainMotor,
                secondaryMotor,
                motorsVoltageSignal,
                mainMotorPositionSignal,
                secondaryMotorPositionSignal,

        );
    }

}

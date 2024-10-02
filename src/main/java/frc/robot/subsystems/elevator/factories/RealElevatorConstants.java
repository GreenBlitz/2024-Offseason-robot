package frc.robot.subsystems.elevator.factories;

import com.revrobotics.CANSparkBase;
import com.revrobotics.SparkLimitSwitch;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import edu.wpi.first.math.filter.Debouncer;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.digitalinput.supplied.SuppliedDigitalInput;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.cansparkmax.SparkMaxDoubleSignal;
import frc.robot.subsystems.elevator.ElevatorStuff;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class RealElevatorConstants {

	private final static double DEBOUNCE_TIME_SECONDS = 0.05;

	private final static Debouncer.DebounceType DEBOUNCE_TYPE = Debouncer.DebounceType.kBoth;

	private final static CANSparkBase.SoftLimitDirection SOFT_LIMIT_DIRECTION = CANSparkBase.SoftLimitDirection.kReverse;

	private final static float SOFT_LIMIT_VALUE = 0;

	private final static double DEBOUNCE_TIME_PHYSICAL_LIMIT = 0.03;

	private final static double GEAR_RATIO = .3; // TODO: check this later

	public final static double MAXIMUM_MOTORS_DELTA = 0.01;

	private final static SparkLimitSwitch.Type REVERSE_LIMIT_SWITCH_TYPE = SparkLimitSwitch.Type.kNormallyOpen;

	public static ElevatorStuff generateElevatorStuff(String logPath) {
		SparkMaxWrapper mainSparkMaxWrapper = new SparkMaxWrapper(IDs.ELEVATOR_FIRST_MOTOR);
		SparkMaxWrapper secondarySparkMaxWrapper = new SparkMaxWrapper(IDs.ELEVATOR_SECOND_MOTOR);

		ControllableMotor mainMotor = new BrushlessSparkMAXMotor(logPath, mainSparkMaxWrapper, new SysIdRoutine.Config());

		IMotor secondaryMotor = new BrushlessSparkMAXMotor(logPath, secondarySparkMaxWrapper, new SysIdRoutine.Config());

		Supplier<Double> mainMotorPosition = () -> mainSparkMaxWrapper.getEncoder().getPosition();
		SparkMaxDoubleSignal mainMotorPositionSignal = new SparkMaxDoubleSignal("motor position", mainMotorPosition);
		mainSparkMaxWrapper.setSoftLimit(SOFT_LIMIT_DIRECTION, SOFT_LIMIT_VALUE);

		Supplier<Double> secondaryMotorPosition = () -> secondarySparkMaxWrapper.getEncoder().getPosition();
		SparkMaxDoubleSignal secondaryMotorPositionSignal = new SparkMaxDoubleSignal("motor position", secondaryMotorPosition);
		mainSparkMaxWrapper.setSoftLimit(SOFT_LIMIT_DIRECTION, SOFT_LIMIT_VALUE);

		Supplier<Double> motorsVoltage = () -> (mainSparkMaxWrapper.getBusVoltage() * secondarySparkMaxWrapper.getAppliedOutput());
		SparkMaxDoubleSignal motorsVoltageSignal = new SparkMaxDoubleSignal("motor position", motorsVoltage);

		secondarySparkMaxWrapper.follow(mainSparkMaxWrapper);

		BooleanSupplier inLimitSwitch = () -> mainSparkMaxWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).isPressed();
		mainSparkMaxWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).enableLimitSwitch(true);
		IDigitalInput limitSwitchDigitalInputs = new SuppliedDigitalInput(inLimitSwitch, DEBOUNCE_TYPE, DEBOUNCE_TIME_SECONDS);

		return new ElevatorStuff(
			logPath,
			logPath + "/digitalInputs",
			mainMotor,
			motorsVoltageSignal,
			mainMotorPositionSignal,
			secondaryMotorPositionSignal,
			limitSwitchDigitalInputs
		);
	}

}

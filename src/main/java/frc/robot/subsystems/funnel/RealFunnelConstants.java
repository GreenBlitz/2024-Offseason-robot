package frc.robot.subsystems.funnel;

import com.revrobotics.SparkLimitSwitch;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.digitalinput.supplied.SuppliedDigitalInput;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.cansparkmax.SparkMaxAngleSignal;
import frc.robot.hardware.signal.cansparkmax.SparkMaxDoubleSignal;
import frc.utils.AngleUnit;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class RealFunnelConstants {

	private final static double DEBOUNCE_TIME_SECONDS = 0.05;
	private final static Debouncer.DebounceType DEBOUNCE_TYPE = Debouncer.DebounceType.kBoth;

	private final static SparkLimitSwitch.Type REVERSE_LIMIT_SWITCH_TYPE = SparkLimitSwitch.Type.kNormallyOpen;

	public static FunnelStuff generateFunnelStuff(String logPath) {
		SparkMaxWrapper bigFunnelWrapper = new SparkMaxWrapper(IDs.BIG_FUNNEL);
		SparkMaxWrapper middleFunnelWrapper = new SparkMaxWrapper(IDs.MIDDLE_FUNNEL);
		SysIdRoutine.Config bigFunnelConfig = new SysIdRoutine.Config();
		SysIdRoutine.Config middleFunnelConfig = new SysIdRoutine.Config();

		BrushlessSparkMAXMotor bigFunnelMotor = new BrushlessSparkMAXMotor(logPath, bigFunnelWrapper, bigFunnelConfig);
		BrushlessSparkMAXMotor middleFunnelMotor = new BrushlessSparkMAXMotor(logPath, middleFunnelWrapper, middleFunnelConfig);

		Supplier<Double> bigFunnelVoltage = () -> bigFunnelWrapper.getBusVoltage() * bigFunnelWrapper.getAppliedOutput();
		SparkMaxDoubleSignal bigFunnelVoltageSignal = new SparkMaxDoubleSignal("bigFunnelVoltage", bigFunnelVoltage);
		Supplier<Double> middleFunnelVoltage = () -> middleFunnelWrapper.getBusVoltage() * middleFunnelWrapper.getAppliedOutput();
		SparkMaxDoubleSignal middleFunnelVoltageSignal = new SparkMaxDoubleSignal("bigFunnelVoltage", middleFunnelVoltage);

		Supplier<Double> bigFunnelPosition = () -> bigFunnelWrapper.getEncoder().getPosition();
		Supplier<Double> middleFunnelPosition = () -> middleFunnelWrapper.getEncoder().getPosition();
		SparkMaxAngleSignal bigFunnelAngleSignal = new SparkMaxAngleSignal("bigFunnelPosition", bigFunnelPosition, AngleUnit.ROTATIONS);
		SparkMaxAngleSignal middleFunnelAngleSignal = new SparkMaxAngleSignal("middleFunnelPosition", middleFunnelPosition, AngleUnit.ROTATIONS);

		BooleanSupplier isBeamBroke = () -> middleFunnelWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).isPressed();
		middleFunnelWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).enableLimitSwitch(false);
		SuppliedDigitalInput beamBreaker = new SuppliedDigitalInput(isBeamBroke, DEBOUNCE_TYPE, DEBOUNCE_TIME_SECONDS);

		return new FunnelStuff(
			logPath,
			bigFunnelMotor,
			middleFunnelMotor,
			bigFunnelVoltageSignal,
			bigFunnelAngleSignal,
			middleFunnelVoltageSignal,
			middleFunnelAngleSignal,
			beamBreaker
		);
	}

}

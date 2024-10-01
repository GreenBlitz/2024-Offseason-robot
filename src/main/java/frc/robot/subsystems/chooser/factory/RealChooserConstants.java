package frc.robot.subsystems.chooser.factory;

import com.revrobotics.SparkLimitSwitch;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.digitalinput.supplied.SuppliedDigitalInput;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.cansparkmax.SparkMaxAngleSignal;
import frc.robot.hardware.signal.cansparkmax.SparkMaxDoubleSignal;
import frc.robot.subsystems.chooser.ChooserStuff;
import frc.utils.AngleUnit;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class RealChooserConstants {

	private final static double DEBOUNCE_TIME = 0.05;
	private final static Debouncer.DebounceType DEBOUNCE_TYPE = Debouncer.DebounceType.kBoth;

	private final static SparkLimitSwitch.Type REVERSE_LIMIT_SWITCH_TYPE = SparkLimitSwitch.Type.kNormallyOpen;

	public static ChooserStuff generateChooserStuff(String logPath) {
		SparkMaxWrapper wrapper = new SparkMaxWrapper(IDs.CANSparkMAXIDs.CHOOSER);
		SysIdRoutine.Config config = new SysIdRoutine.Config();
		BrushlessSparkMAXMotor motor = new BrushlessSparkMAXMotor(logPath, wrapper, config);

		Supplier<Double> voltage = () -> wrapper.getBusVoltage() * wrapper.getAppliedOutput();
		SparkMaxDoubleSignal voltageSignal = new SparkMaxDoubleSignal("voltage", voltage);

		Supplier<Double> position = () -> wrapper.getEncoder().getPosition();
		SparkMaxAngleSignal positionSignal = new SparkMaxAngleSignal("position", position, AngleUnit.ROTATIONS);

		BooleanSupplier isBeamBroke = () -> wrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).isPressed();
		wrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).enableLimitSwitch(false);
		SuppliedDigitalInput beamBreaker = new SuppliedDigitalInput(isBeamBroke, DEBOUNCE_TYPE, DEBOUNCE_TIME);

		return new ChooserStuff(logPath, motor, voltageSignal, positionSignal, beamBreaker);
	}

}

package frc.robot.subsystems.chooser.factory;

import com.revrobotics.CANSparkBase;
import com.revrobotics.SparkLimitSwitch;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.digitalinput.supplied.SuppliedDigitalInput;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.supplied.SuppliedDoubleSignal;
import frc.robot.subsystems.chooser.ChooserStuff;

import java.util.function.BooleanSupplier;

public class RealChooserConstants {

	private final static double DEBOUNCE_TIME_SECONDS = 0.05;
	private final static Debouncer.DebounceType DEBOUNCE_TYPE = Debouncer.DebounceType.kBoth;
	private final static SparkLimitSwitch.Type REVERSE_LIMIT_SWITCH_TYPE = SparkLimitSwitch.Type.kNormallyOpen;

	public static ChooserStuff generateChooserStuff(String logPath) {
		SparkMaxWrapper sparkMAXWrapper = new SparkMaxWrapper(IDs.CANSparkMAXIDs.CHOOSER);
		SysIdRoutine.Config config = new SysIdRoutine.Config();

		sparkMAXWrapper.setSmartCurrentLimit(30);
		sparkMAXWrapper.setIdleMode(CANSparkBase.IdleMode.kCoast);

		BrushlessSparkMAXMotor motor = new BrushlessSparkMAXMotor(logPath, sparkMAXWrapper, config);

		SuppliedDoubleSignal voltageSignal = new SuppliedDoubleSignal("voltage", sparkMAXWrapper::getVoltage);

		BooleanSupplier isBeamBroken = () -> sparkMAXWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).isPressed();
		sparkMAXWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).enableLimitSwitch(false);
		SuppliedDigitalInput beamBreaker = new SuppliedDigitalInput(isBeamBroken, DEBOUNCE_TYPE, DEBOUNCE_TIME_SECONDS);

		return new ChooserStuff(logPath, motor, voltageSignal, beamBreaker);
	}

}

package frc.robot.subsystems.funnel.factory;

import com.revrobotics.CANSparkBase;
import com.revrobotics.SparkLimitSwitch;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.digitalinput.channeled.ChanneledDigitalInput;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.supplied.SuppliedDoubleSignal;
import frc.robot.subsystems.funnel.FunnelConstants;
import frc.robot.subsystems.funnel.FunnelStuff;


public class RealFunnelConstants {

	private final static double DEBOUNCE_TIME_SECONDS = 0.1;

	private final static Debouncer.DebounceType DEBOUNCE_TYPE = Debouncer.DebounceType.kBoth;

	private final static SparkLimitSwitch.Type REVERSE_LIMIT_SWITCH_TYPE = SparkLimitSwitch.Type.kNormallyOpen;

	private final static boolean ENABLE_LIMIT_SWITCH = false;

	public static FunnelStuff generateFunnelStuff(String logPath) {
		SparkMaxWrapper sparkMAXWrapper = new SparkMaxWrapper(IDs.CANSparkMAXIDs.FUNNEL);
		configMotor(sparkMAXWrapper);
		BrushlessSparkMAXMotor motor = new BrushlessSparkMAXMotor(logPath, sparkMAXWrapper, new SysIdRoutine.Config());

		SuppliedDoubleSignal voltageSignal = new SuppliedDoubleSignal("voltage", sparkMAXWrapper::getVoltage);

		sparkMAXWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).enableLimitSwitch(ENABLE_LIMIT_SWITCH);
		ChanneledDigitalInput shooterBeamBreaker = new ChanneledDigitalInput(1, DEBOUNCE_TIME_SECONDS, true);

		return new FunnelStuff(logPath, motor, voltageSignal, shooterBeamBreaker);
	}

	protected static void configMotor(SparkMaxWrapper motor) {
		motor.setIdleMode(CANSparkBase.IdleMode.kCoast);
		motor.setSmartCurrentLimit(30);
		motor.getEncoder().setPositionConversionFactor(FunnelConstants.GEAR_RATIO);
		motor.getEncoder().setVelocityConversionFactor(FunnelConstants.GEAR_RATIO);
		motor.setInverted(true);
	}

}

package frc.robot.subsystems.intake.pivot.factory;

import com.revrobotics.CANSparkBase;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.request.cansparkmax.SparkMaxAngleRequest;
import frc.robot.hardware.signal.supplied.SuppliedAngleSignal;
import frc.robot.hardware.signal.supplied.SuppliedDoubleSignal;
import frc.robot.subsystems.intake.pivot.PivotConstants;
import frc.robot.subsystems.intake.pivot.PivotStuff;
import frc.utils.AngleUnit;
import org.littletonrobotics.junction.Logger;

import java.util.function.Function;

import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

public class RealPivotConstants {
	
	private static final double GEAR_RATIO = 1/((7.0 / 1) * (5.0 / 1) * (60.0 / 16));

	public static final int POSITION_PID_SLOT = 0;

	private static final ArmFeedforward FEEDFORWARD_CALCULATOR = new ArmFeedforward(0.2465, 10.886, 5.088);

	//@formatter:off
	private static final Function<Rotation2d, Double> FEEDFORWARD_FUNCTION =
		position -> FEEDFORWARD_CALCULATOR.calculate(position.getRadians(), 0);
	//@formatter:on

	private static void configMotor(SparkMaxWrapper sparkMaxWrapper) {
		sparkMaxWrapper.getEncoder().setPositionConversionFactor(GEAR_RATIO);
		sparkMaxWrapper.getEncoder().setVelocityConversionFactor(GEAR_RATIO);
		sparkMaxWrapper.getPIDController().setP(0);
		sparkMaxWrapper.getPIDController().setI(0);
		sparkMaxWrapper.getPIDController().setD(0);
		sparkMaxWrapper.setSmartCurrentLimit(40);
		sparkMaxWrapper.setIdleMode(CANSparkBase.IdleMode.kCoast);
		sparkMaxWrapper.setInverted(true);
		sparkMaxWrapper.setSoftLimit(CANSparkBase.SoftLimitDirection.kForward, (float) PivotConstants.FORWARD_SOFT_LIMIT.getRotations());
		sparkMaxWrapper.enableSoftLimit(CANSparkBase.SoftLimitDirection.kForward, true);
		sparkMaxWrapper.setSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, (float) PivotConstants.REVERSE_SOFT_LIMIT.getRotations());
		sparkMaxWrapper.enableSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, true);
	}
	
	private static SysIdRoutine.Config generateSysidConfig() {
		return new SysIdRoutine.Config(
				Volts.of(0.5).per(Seconds.of(1)),
				Volts.of(3),
				Seconds.of(10),
				(state) -> Logger.recordOutput("state", state.toString())
		);
	}

	protected static PivotStuff generatePivotStuff(String logPath) {
		SparkMaxWrapper sparkMaxWrapper = new SparkMaxWrapper(IDs.CANSparkMAXIDs.PIVOT);
		configMotor(sparkMaxWrapper);

		BrushlessSparkMAXMotor motor = new BrushlessSparkMAXMotor(PivotConstants.LOG_PATH, sparkMaxWrapper,generateSysidConfig());

		SuppliedDoubleSignal voltageSignal = new SuppliedDoubleSignal("voltage", sparkMaxWrapper::getVoltage);
		SuppliedAngleSignal positionSignal = new SuppliedAngleSignal("position", sparkMaxWrapper.getEncoder()::getPosition, AngleUnit.ROTATIONS);

		SuppliedDoubleSignal positionSignalSysid = new SuppliedDoubleSignal("position sydid",sparkMaxWrapper.getEncoder()::getPosition);
		SuppliedDoubleSignal pvelocitySignalSysid = new SuppliedDoubleSignal("velocity sydid",()->sparkMaxWrapper.getEncoder().getVelocity()/60);
		
		SparkMaxAngleRequest positionRequest = new SparkMaxAngleRequest(
			positionSignal.getLatestValue(),
			SparkMaxAngleRequest.SparkAngleRequestType.POSITION,
			POSITION_PID_SLOT,
			FEEDFORWARD_FUNCTION
		);

		return new PivotStuff(logPath, motor, voltageSignal, positionSignal, positionRequest,positionSignalSysid,pvelocitySignalSysid);
	}

}

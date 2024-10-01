package frc.robot.subsystems.flywheel.factory;

import com.ctre.phoenix6.controls.VelocityVoltage;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.constants.IDs;
import frc.robot.hardware.motor.sparkmax.BrushlessSparkMAXMotor;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.request.phoenix6.Phoenix6AngleRequest;
import frc.robot.hardware.signal.cansparkmax.SparkMaxAngleSignal;
import frc.robot.hardware.signal.cansparkmax.SparkMaxDoubleSignal;
import frc.robot.subsystems.flywheel.FlywheelComponents;
import frc.utils.AngleUnit;

import java.util.function.Supplier;

public class RealFlywheelConstants {

	public static FlywheelComponents generateFlywheelComponents(String logpath, boolean isTopMotorInverted) {
		SparkMaxWrapper topSparkMaxWrapper = new SparkMaxWrapper(IDs.CANSparkMaxIDs.TOP_FLYWHEEL_MOTOR);
		SparkMaxWrapper bottomSparkMaxWrapper = new SparkMaxWrapper(IDs.CANSparkMaxIDs.BOTTOM_FLYWHEEL_MOTOR);

		SysIdRoutine.Config topConfig = new SysIdRoutine.Config();
		SysIdRoutine.Config bottomConfig = new SysIdRoutine.Config();

		BrushlessSparkMAXMotor topMotor = new BrushlessSparkMAXMotor(logpath, topSparkMaxWrapper, topConfig);
		BrushlessSparkMAXMotor bottomMotor = new BrushlessSparkMAXMotor(logpath, bottomSparkMaxWrapper, bottomConfig);

		Supplier<Double> topMotorVoltageSupplier = () -> topSparkMaxWrapper.getBusVoltage() * topSparkMaxWrapper.getAppliedOutput();
		SparkMaxDoubleSignal topMotorVoltageSignal = new SparkMaxDoubleSignal("Top flywheel motor voltage Signal", topMotorVoltageSupplier);

		Supplier<Double> bottomMotorVoltageSupplier = () -> topSparkMaxWrapper.getBusVoltage() * topSparkMaxWrapper.getAppliedOutput();
		SparkMaxDoubleSignal bottomMotorVoltageSignal = new SparkMaxDoubleSignal(
			"Bottom flywheel motor voltage Signal",
			bottomMotorVoltageSupplier
		);

		Supplier<Double> topMotorVelocitySupplier = () -> topSparkMaxWrapper.getEncoder().getVelocity();
		SparkMaxAngleSignal topMotorVelocitySignal = new SparkMaxAngleSignal(
			"Top flywheel motor velocity Signal",
			topMotorVelocitySupplier,
			AngleUnit.ROTATIONS
		);

		Supplier<Double> bottomMotorVelocitySupplier = () -> bottomSparkMaxWrapper.getEncoder().getVelocity();
		SparkMaxAngleSignal bottomMotorVelocitySignal = new SparkMaxAngleSignal(
			"Bottom flywheel motor velocity Signal",
			bottomMotorVelocitySupplier,
			AngleUnit.ROTATIONS
		);

		Phoenix6AngleRequest topMotorVelocityRequest = new Phoenix6AngleRequest(new VelocityVoltage(0));
		Phoenix6AngleRequest bottomMotorVelocityRequest = new Phoenix6AngleRequest(new VelocityVoltage(0));

		return new FlywheelComponents(
			logpath,
			topMotor,
			bottomMotor,
			isTopMotorInverted,
			!isTopMotorInverted,
			topMotorVoltageSignal,
			bottomMotorVoltageSignal,
			topMotorVelocitySignal,
			bottomMotorVelocitySignal,
			topMotorVelocityRequest,
			bottomMotorVelocityRequest
		);
	}

}

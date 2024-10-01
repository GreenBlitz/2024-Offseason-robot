package frc.robot.subsystems.flywheel;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.request.IRequest;
import frc.robot.hardware.signal.InputSignal;

public record FlywheelComponents(
	String logPath,
	ControllableMotor topMotor,
	ControllableMotor bottomMotor,
	boolean isTopMotorInverted,
	boolean isBottomMotorInverted,
	InputSignal<Double> topMotorVoltageSignal,
	InputSignal<Double> bottomMotorVoltageSignal,
	InputSignal<Rotation2d> topMotorVelocitySignal,
	InputSignal<Rotation2d> bottomMotorVelocitySignal,
	IRequest<Rotation2d> topMotorVelocityRequest,
	IRequest<Rotation2d> bottomMotorVelocityRequest

) {}

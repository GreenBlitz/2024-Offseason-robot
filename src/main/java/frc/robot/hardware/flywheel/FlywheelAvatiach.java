package frc.robot.hardware.flywheel;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.signal.InputSignal;

public record FlywheelAvatiach(
	String logPath,
	IMotor topMotor,
	IMotor bottomMotor,
	boolean isTopMotorInverted,
	boolean isBottomMotorInverted,
	InputSignal<Double> topMotorVoltageSignal,
	InputSignal<Double> bottomMotorVoltageSignal,
	InputSignal<Double> topMotorVelocitySignal,
	InputSignal<Double> bottomMotorVelocitySignal,
    PIDController pidController

) {}

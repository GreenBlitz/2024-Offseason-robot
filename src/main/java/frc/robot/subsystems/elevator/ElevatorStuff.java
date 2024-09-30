package frc.robot.subsystems.elevator;

import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.signal.InputSignal;

public record ElevatorStuff(
	String logPath,
	String digitalInputsLogPath,
	ControllableMotor mainMotor,
	InputSignal<Double> voltageSignal,
	InputSignal<Double> mainMotorPositionSignal,
	InputSignal<Double> secondaryMotorPositionSignal,
	IDigitalInput digitalInput
) {}

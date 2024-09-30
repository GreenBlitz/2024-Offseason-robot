package frc.robot.subsystems.elevator;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.signal.InputSignal;

public record ElevatorStuff(
    String logPath,
    String digitalInputsLogPath,
    IMotor mainMotor,
    IMotor secondaryMotor,
    InputSignal<Double> voltageSignal,
    InputSignal<Rotation2d> mainMotorPositionSignal,
    InputSignal<Rotation2d> secondaryMotorPositionSignal,
    IDigitalInput digitalInput
) { }

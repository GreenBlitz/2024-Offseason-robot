package frc.robot.subsystems.elevator;

import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;

public class Elevator extends GBSubsystem {

    private final IMotor mainMotor;
    private final IMotor secondaryMotor;
    private final IDigitalInput digitalInput;
    private final DigitalInputInputsAutoLogged digitalInputsInputs;
    private final ElevatorCommandBuilder commandBuilder;
    private final ElevatorStuff elevatorStuff;
    private double TargetPosition;

    public Elevator(ElevatorStuff elevatorStuff) {
        super(elevatorStuff.logPath());
        this.mainMotor = elevatorStuff.mainMotor();
        this.secondaryMotor = elevatorStuff.secondaryMotor();
        this.digitalInput = elevatorStuff.digitalInput();
        this.digitalInputsInputs = new DigitalInputInputsAutoLogged();
        this.elevatorStuff = elevatorStuff;
        this.commandBuilder = new ElevatorCommandBuilder();
    }

    public ElevatorCommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    public boolean isStopped() {
        return digitalInputsInputs.debouncedValue;
    }

    public double synchronizingDelta() {
        return elevatorStuff.mainMotorPositionSignal().getLatestValue().getRadians() - elevatorStuff.secondaryMotorPositionSignal().getLatestValue().getRadians();
    }



    public void updateInputs() {
        digitalInput.updateInputs(digitalInputsInputs);
        mainMotor.updateSignals();
    }

}

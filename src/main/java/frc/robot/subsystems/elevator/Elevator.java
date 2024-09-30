package frc.robot.subsystems.elevator;

import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Elevator extends GBSubsystem {

    private final DigitalInputInputsAutoLogged digitalInputsInputs;
    private final ElevatorCommandBuilder commandBuilder;
    private final ElevatorStuff elevatorStuff;
    private double targetPosition;

    public Elevator(ElevatorStuff elevatorStuff) {
        super(elevatorStuff.logPath());
        this.digitalInputsInputs = new DigitalInputInputsAutoLogged();
        this.elevatorStuff = elevatorStuff;
        this.commandBuilder = new ElevatorCommandBuilder(this);
    }

    public ElevatorCommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    public void setPower(double target) {
        targetPosition = target;
    }

    public void stop() {
        elevatorStuff.mainMotor().stop();
    }

    public void setBrake(boolean brake) {
        elevatorStuff.mainMotor().setBrake(brake);
    }

    public void setTargetPosition(double position) {
        targetPosition = position;
    }

    public double getTargetPosition() {
        return targetPosition;
    }

    public boolean isPhysicallyStopped() {
        return digitalInputsInputs.debouncedValue;
    }

    public double getSynchronizingDelta() {
        return elevatorStuff.mainMotorPositionSignal().getLatestValue().getRadians() - elevatorStuff.secondaryMotorPositionSignal().getLatestValue().getRadians();
    }

    public boolean emergencyStop() {
        return getSynchronizingDelta() >= ElevatorConstants.MAXIMUM_MOTORS_DELTA || isPhysicallyStopped();
    }

    public void checkEmergancyStop() {
        if (emergencyStop()) {
            stop();
        }
    }

    public void updateInputs() {
        elevatorStuff.digitalInput().updateInputs(digitalInputsInputs);
        elevatorStuff.mainMotor().updateSignals();
    }

    @Override
    protected void subsystemPeriodic() {
        Logger.processInputs(elevatorStuff.digitalInputsLogPath(), digitalInputsInputs);
        elevatorStuff.mainMotor().;
    }
}

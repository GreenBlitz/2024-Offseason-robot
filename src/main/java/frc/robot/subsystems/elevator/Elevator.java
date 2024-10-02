package frc.robot.subsystems.elevator;

import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.request.IRequest;
import frc.robot.hardware.request.cansparkmax.SparkMaxDoubleRequest;
import frc.robot.subsystems.elevator.factories.RealElevatorConstants;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Elevator extends GBSubsystem {

	private final DigitalInputInputsAutoLogged digitalInputsInputs;
	private final ElevatorCommandBuilder commandBuilder;
	private final IRequest positionRequest;
	private double targetPosition;

	private final ElevatorStuff elevatorStuff;
	private final ControllableMotor mainMotor;
	private final IDigitalInput limitSwitch;

	public Elevator(ElevatorStuff elevatorStuff) {
		super(elevatorStuff.logPath());

		this.mainMotor = elevatorStuff.mainMotor();
		this.limitSwitch = elevatorStuff.digitalInput();

		this.digitalInputsInputs = new DigitalInputInputsAutoLogged();
		this.elevatorStuff = elevatorStuff;
		this.commandBuilder = new ElevatorCommandBuilder(this);

		this.positionRequest = new SparkMaxDoubleRequest(targetPosition, SparkMaxDoubleRequest.SparkDoubleRequestType.CURRENT, 0);
		elevatorStuff.mainMotor().applyDoubleRequest(positionRequest);
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

	public void updateInputs() {
		limitSwitch.updateInputs(digitalInputsInputs);
		mainMotor.updateSignals();
	}

	@Override
	protected void subsystemPeriodic() {
		updateInputs();
		Logger.processInputs(elevatorStuff.digitalInputsLogPath(), digitalInputsInputs);
		positionRequest.withSetPoint(targetPosition);
	}

}

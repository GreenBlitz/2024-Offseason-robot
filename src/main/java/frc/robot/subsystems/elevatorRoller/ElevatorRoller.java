package frc.robot.subsystems.elevatorRoller;

import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class ElevatorRoller extends GBSubsystem {

	private final IMotor motor;
	private final IDigitalInput elevatorRollerDigitalInput;
	private final DigitalInputInputsAutoLogged elevatorRollerDigitalInputInputs;
	private final ElevatorRollerStuff elevatorRollerStuff;
	private final ElevatorRollerCommandsBuilder commandsBuilder;

	public ElevatorRoller(ElevatorRollerStuff elevatorRollerStuff) {
		super(elevatorRollerStuff.logPath());
		this.motor = elevatorRollerStuff.motor();
		this.elevatorRollerDigitalInput = elevatorRollerStuff.digitalInput();
		this.elevatorRollerStuff = elevatorRollerStuff;
		this.elevatorRollerDigitalInputInputs = new DigitalInputInputsAutoLogged();

		this.commandsBuilder = new ElevatorRollerCommandsBuilder(this);
		updateInputs();
	}

	public ElevatorRollerCommandsBuilder getCommandsBuilder() {
		return commandsBuilder;
	}

	public void updateInputs() {
		elevatorRollerDigitalInput.updateInputs(elevatorRollerDigitalInputInputs);
		motor.updateSignals(elevatorRollerStuff.motorVoltage());
		Logger.processInputs(elevatorRollerStuff.digitalInputLogPath(), elevatorRollerDigitalInputInputs);
	}

	public boolean isNoteIn() {
		return elevatorRollerDigitalInputInputs.debouncedValue;
	}

	@Override
	protected void subsystemPeriodic() {
		updateInputs();
		Logger.recordOutput(elevatorRollerStuff.logPath() + "IsNoteInElevatorRoller", isNoteIn());
	}

	protected void setPower(double power) {
		motor.setPower(power);
	}

	protected void stop() {
		motor.stop();
	}

	protected void setBrake(boolean brake) {
		motor.setBrake(brake);
	}

}

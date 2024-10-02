package frc.robot.subsystems.chooser;

import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Chooser extends GBSubsystem {

	private final IMotor motor;
	private final ChooserStuff chooserStuff;
	private final ChooserCommandsBuilder commandsBuilder;
	private final IDigitalInput digitalInput;
	private final DigitalInputInputsAutoLogged digitalInputInputs;

	public Chooser(ChooserStuff chooserStuff) {
		super(chooserStuff.logPath());
		this.motor = chooserStuff.motor();
		this.digitalInput = chooserStuff.digitalInput();
		this.chooserStuff = chooserStuff;
		this.digitalInputInputs = new DigitalInputInputsAutoLogged();

		this.commandsBuilder = new ChooserCommandsBuilder(this);

		updateInputs();
	}

	public ChooserCommandsBuilder getCommandsBuilder() {
		return commandsBuilder;
	}

	public boolean isNoteIn() {
		return digitalInputInputs.debouncedValue;
	}

	public void stop() {
		motor.stop();
	}

	public void setPower(double power) {
		motor.setPower(power);
	}

	public void updateInputs() {
		digitalInput.updateInputs(digitalInputInputs);
		motor.updateSignals(chooserStuff.voltageSignal());
	}

	@Override
	protected void subsystemPeriodic() {
		updateInputs();
		Logger.processInputs(chooserStuff.digitalInputLogPath(), digitalInputInputs);
		Logger.recordOutput("isNoteIn", digitalInputInputs.debouncedValue);
	}

}

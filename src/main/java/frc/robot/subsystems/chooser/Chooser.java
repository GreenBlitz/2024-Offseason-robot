package frc.robot.subsystems.chooser;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Chooser extends GBSubsystem {

	private final IMotor motor;
	private final ChooserStuff chooserStuff;
	private final ChooserCommandsBuilder commandBuilder;
	private final IDigitalInput digitalInput;
	private final DigitalInputInputsAutoLogged digitalInputInputs;

	public Chooser(ChooserStuff chooserStuff) {
		super(chooserStuff.logPath());
		this.motor = chooserStuff.motor();
		this.digitalInput = chooserStuff.digitalInput();
		this.chooserStuff = chooserStuff;
		this.digitalInputInputs = new DigitalInputInputsAutoLogged();

		this.commandBuilder = new ChooserCommandsBuilder(this);
		updateInputs();
	}

	public ChooserCommandsBuilder getCommandBuilder() {
		return commandBuilder;
	}

	public boolean isNoteIn() {
		return digitalInputInputs.debouncedValue;
	}

	public void updateInputs() {
		digitalInput.updateInputs(digitalInputInputs);
		motor.updateSignals(chooserStuff.voltageSignal(), chooserStuff.positionSignal());
	}

	public void setPower(double power) {
		motor.setPower(power);
	}

	public void stop() {
		motor.stop();
	}

	public void setBrake(boolean brake) {
		motor.setBrake(brake);
	}

	@Override
	protected void subsystemPeriodic() {
		updateInputs();
		Logger.processInputs(chooserStuff.digitalInputLogPath(), digitalInputInputs);
	}

}

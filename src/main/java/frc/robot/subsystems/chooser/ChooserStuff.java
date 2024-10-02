package frc.robot.subsystems.chooser;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.signal.InputSignal;

public record ChooserStuff(
	String logPath,
	String digitalInputLogPath,
	IMotor motor,
	InputSignal<Double> voltageSignal,
	IDigitalInput digitalInput
) {

	public ChooserStuff(
		String logPath,
		IMotor motor,
		InputSignal<Double> voltageSignal,
		IDigitalInput digitalInput
	) {
		this(logPath, logPath + "digitalInput", motor, voltageSignal, digitalInput);
	}

}

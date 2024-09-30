package frc.robot.subsystems.funnel;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Funnel extends GBSubsystem {

	private final IMotor motor;
	private final FunnelAvatiach funnelAvatiach;
	private final IDigitalInput shooterDigitalInput;
	private final IDigitalInput ampDigitalInput;
	private final DigitalInputInputsAutoLogged shooterDigitalInputInputs;
	private final DigitalInputInputsAutoLogged ampDigitalInputInputs;
	private Rotation2d targetPosition;

	public Funnel(FunnelAvatiach funnelAvatiach) {
		super(funnelAvatiach.logPath());
		this.motor = funnelAvatiach.motor();
		this.shooterDigitalInput = funnelAvatiach.shooterDigitalInput();
		this.ampDigitalInput = funnelAvatiach.ampDigitalInput();
		this.funnelAvatiach = funnelAvatiach;
		this.shooterDigitalInputInputs = new DigitalInputInputsAutoLogged();
		this.ampDigitalInputInputs = new DigitalInputInputsAutoLogged();

		this.targetPosition = new Rotation2d();
		update();
	}

	public boolean isObjectInShooter() {
		return shooterDigitalInputInputs.debouncedValue;
	}

	public boolean isObjectInAmp() {
		return ampDigitalInputInputs.debouncedValue;
	}

	public void update() {
		shooterDigitalInput.updateInputs(shooterDigitalInputInputs);
		ampDigitalInput.updateInputs(ampDigitalInputInputs);
		motor.updateSignals(funnelAvatiach.voltageSignal(), funnelAvatiach.positionSignal());
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

	public Rotation2d getPosition() {
		return funnelAvatiach.positionSignal().getLatestValue();
	}

	public void setTargetPosition(double rotations) {
		this.targetPosition = Rotation2d.fromRotations(getPosition().getRotations() + rotations);
	}

	public boolean isAtPosition(Rotation2d position){
		return (getPosition().getRotations() - position.getRotations() <= 5);
	}

	public boolean isPastPosition() {
		return getPosition().getRotations() > targetPosition.getRotations();
	}

	@Override
	protected void subsystemPeriodic() {
		update();
		Logger.processInputs(funnelAvatiach.shooterDigitalInputLogPath(), shooterDigitalInputInputs);
		Logger.processInputs(funnelAvatiach.ampDigitalInputLogPath(), ampDigitalInputInputs);
		Logger.recordOutput(funnelAvatiach.logPath() + "IsObjectInShooter", isObjectInShooter());
		Logger.recordOutput(funnelAvatiach.logPath() + "IsObjectInAmp", isObjectInAmp());
	}

}

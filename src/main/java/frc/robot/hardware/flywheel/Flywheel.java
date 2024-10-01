package frc.robot.hardware.flywheel;

import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;

public class Flywheel extends GBSubsystem {

	private final FlywheelAvatiach flywheelAvatiach;

	private final IMotor topMotor;
	private final IMotor bottomMotor;

	public Flywheel(FlywheelAvatiach flywheelAvatiach) {
		super(flywheelAvatiach.logPath());

		this.flywheelAvatiach = flywheelAvatiach;
		this.topMotor = flywheelAvatiach.topMotor();
		this.bottomMotor = flywheelAvatiach.bottomMotor();
	}

	public double getTopMotorVoltage() {
		return flywheelAvatiach.topMotorVoltageSignal().getLatestValue();
	}

	public double getBottomMotorVoltage() {
		return flywheelAvatiach.bottomMotorVoltageSignal().getLatestValue();
	}

	public void setPower(double power) {
		topMotor.setPower(power);
		bottomMotor.setPower(power);
	}

	public void setVoltage(double voltage) {
		topMotor.setPower(voltage);
	}

	public void setVelocity(double power) {
		// to do
	}

	public void stop() {
		topMotor.setPower(0);
		bottomMotor.setPower(0);
	}

	public void setBreak(boolean isBreak) {
		topMotor.setBrake(isBreak);
		bottomMotor.setBrake(isBreak);
	}

	public void updateInputs() {
		topMotor.updateSignals(flywheelAvatiach.topMotorVoltageSignal(), flywheelAvatiach.topMotorVelocitySignal());
		bottomMotor.updateSignals(flywheelAvatiach.bottomMotorVoltageSignal(), flywheelAvatiach.bottomMotorVelocitySignal());
	}


}

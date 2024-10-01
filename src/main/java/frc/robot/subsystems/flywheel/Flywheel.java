package frc.robot.subsystems.flywheel;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.motor.ControllableMotor;
import frc.utils.GBSubsystem;

public class Flywheel extends GBSubsystem {

	private final FlywheelComponents flywheelComponents;
	private final ControllableMotor topMotor;
	private final ControllableMotor bottomMotor;
	private final FlywheelCommandBuilder commandBuilder;

	public Flywheel(FlywheelComponents flywheelComponents) {
		super(flywheelComponents.logPath());

		this.flywheelComponents = flywheelComponents;
		this.topMotor = flywheelComponents.topMotor();
		this.bottomMotor = flywheelComponents.bottomMotor();
		this.commandBuilder = new FlywheelCommandBuilder(this);
	}

	public double getTopMotorVoltage() {
		return flywheelComponents.topMotorVoltageSignal().getLatestValue();
	}

	public double getBottomMotorVoltage() {
		return flywheelComponents.bottomMotorVoltageSignal().getLatestValue();
	}

	public void setPower(double power) {
		topMotor.setPower(power);
		bottomMotor.setPower(power);
	}

	public void setTargetVelocity(Rotation2d targetVelocityRPS) {
		topMotor.applyAngleRequest(flywheelComponents.topMotorVelocityRequest().withSetPoint(targetVelocityRPS));
		bottomMotor.applyAngleRequest(flywheelComponents.bottomMotorVelocityRequest().withSetPoint(targetVelocityRPS));
	}

	public boolean isAtVelocity(Rotation2d targetVelocityRPS, Rotation2d velocityTolerance) {
		return MathUtil.isNear(
			targetVelocityRPS.getRotations(),
			flywheelComponents.topMotorVelocitySignal().getLatestValue().getRotations(),
			velocityTolerance.getRotations()
		);
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
		topMotor.updateSignals(flywheelComponents.topMotorVoltageSignal(), flywheelComponents.topMotorVelocitySignal());
		bottomMotor.updateSignals(flywheelComponents.bottomMotorVoltageSignal(), flywheelComponents.bottomMotorVelocitySignal());
	}

	@Override
	protected void subsystemPeriodic() {
		updateInputs();
	}

}

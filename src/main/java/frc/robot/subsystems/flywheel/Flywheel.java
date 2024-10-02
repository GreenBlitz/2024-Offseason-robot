package frc.robot.subsystems.flywheel;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.motor.ControllableMotor;
import frc.utils.GBSubsystem;

public class Flywheel extends GBSubsystem {

	private final TopFlywheelComponents topFlywheelComponents;
	private final BottomFlywheelComponents bottomFlywheelComponents;

	private final ControllableMotor topMotor;
	private final ControllableMotor bottomMotor;
	private final FlywheelCommandBuilder commandBuilder;

	public Flywheel(TopFlywheelComponents topFlywheelComponents, BottomFlywheelComponents bottomFlywheelComponents) {
		super(FlywheelConstants.LOG_PATH);

		this.topFlywheelComponents = topFlywheelComponents;
		this.bottomFlywheelComponents = bottomFlywheelComponents;
		this.topMotor = topFlywheelComponents.motor();
		this.bottomMotor = bottomFlywheelComponents.motor();
		this.commandBuilder = new FlywheelCommandBuilder(this);

		updateInputs();
	}

	public double getTopMotorVoltage() {
		return topFlywheelComponents.motorVoltageSignal().getLatestValue();
	}

	public double getBottomMotorVoltage() {
		return bottomFlywheelComponents.motorVoltageSignal().getLatestValue();
	}

	public FlywheelCommandBuilder getCommandBuilder() {
		return commandBuilder;
	}

	public void setPower(double power) {
		topMotor.setPower(power);
		bottomMotor.setPower(power);
	}

	public void setTargetVelocity(Rotation2d targetVelocity) {
		topMotor.applyAngleRequest(topFlywheelComponents.motorVelocityRequest().withSetPoint(targetVelocity));
		bottomMotor.applyAngleRequest(bottomFlywheelComponents.motorVelocityRequest().withSetPoint(targetVelocity));
	}

	public boolean isAtVelocity(Rotation2d targetVelocity, Rotation2d velocityTolerance) {
		return MathUtil.isNear(
			targetVelocity.getRotations(),
			topFlywheelComponents.motorVelocitySignal().getLatestValue().getRotations(),
			velocityTolerance.getRotations()
		);
	}

	public void stop() {
		topMotor.stop();
		bottomMotor.stop();
	}

	public void updateInputs() {
		topMotor.updateSignals(topFlywheelComponents.motorVoltageSignal(), topFlywheelComponents.motorVelocitySignal());
		bottomMotor.updateSignals(bottomFlywheelComponents.motorVoltageSignal(), bottomFlywheelComponents.motorVelocitySignal());
	}

	@Override
	protected void subsystemPeriodic() {
		updateInputs();
	}

}

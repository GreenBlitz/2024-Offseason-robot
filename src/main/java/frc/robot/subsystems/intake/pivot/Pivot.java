package frc.robot.subsystems.intake.pivot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.MedianFilter;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.request.IRequest;
import frc.robot.hardware.request.cansparkmax.SparkMaxDoubleRequest;
import frc.robot.subsystems.GBSubsystem;
import frc.utils.calibration.sysid.SysIdCalibrator;
import org.littletonrobotics.junction.Logger;

public class Pivot extends GBSubsystem {

	private final PivotStuff pivotStuff;
	private final ControllableMotor motor;
	private final IRequest<Rotation2d> positionRequest;
	private final MedianFilter resetAngleFilter;
	private final PivotCommandsBuilder commandsBuilder;

	public Pivot(PivotStuff pivotStuff) {
		super(pivotStuff.logPath());
		this.pivotStuff = pivotStuff;
		this.motor = pivotStuff.motor();
		this.positionRequest = pivotStuff.positionRequest();
		this.resetAngleFilter = new MedianFilter(PivotConstants.MEDIAN_FILTER_SIZE);
		this.commandsBuilder = new PivotCommandsBuilder(this);

		motor.resetPosition(PivotConstants.MAXIMUM_ACHIEVABLE_ANGLE);
		updateInputs();
		updateResetFilter();
	}

	private void updateResetFilter() {
		for (int i = 0; i < PivotConstants.MEDIAN_FILTER_SIZE; i++) {
			resetAngleFilter.calculate(pivotStuff.positionSignal().getLatestValue().getRotations());
		}
	}

	public PivotCommandsBuilder getCommandsBuilder() {
		return commandsBuilder;
	}

	public void setBrake(boolean shouldBrake) {
		motor.setBrake(shouldBrake);
	}

	protected void setPower(double power) {
		motor.setPower(power);
	}

	protected void setTargetPosition(Rotation2d targetPosition) {
		motor.applyAngleRequest(positionRequest.withSetPoint(targetPosition));
	}

	protected void stop() {
		motor.stop();
	}

	protected void stayInPlace() {
		motor.applyAngleRequest(positionRequest.withSetPoint(pivotStuff.positionSignal().getLatestValue()));
	}

	public boolean isAtPosition(Rotation2d targetPosition, Rotation2d tolerance) {
		return MathUtil.isNear(targetPosition.getDegrees(), pivotStuff.positionSignal().getLatestValue().getDegrees(), tolerance.getDegrees());
	}

	private void updateInputs() {
		motor.updateSignals(pivotStuff.positionSignal(), pivotStuff.voltageSignal());
		motor.updateSignals(pivotStuff.signals());
	}
	
	public SysIdCalibrator getSysidCalibrator(){
		return new SysIdCalibrator(
				motor.getSysidConfigInfo(),
				this,
				this::setVoltage
		);
	}
	
	public void setVoltage(double voltage){
		motor.applyDoubleRequest(new SparkMaxDoubleRequest(voltage, SparkMaxDoubleRequest.SparkDoubleRequestType.VOLTAGE,0));
	}
	
	@Override
	protected void subsystemPeriodic() {
		if (
			PivotConstants.MAXIMUM_ACHIEVABLE_ANGLE.getRotations()
				< resetAngleFilter.calculate(pivotStuff.positionSignal().getLatestValue().getRotations())
		) {
			motor.resetPosition(PivotConstants.MAXIMUM_ACHIEVABLE_ANGLE);
		}
		
		Logger.recordOutput("pivot pos", pivotStuff.positionSignal().getLatestValue().getDegrees());
		updateInputs();
	}

}

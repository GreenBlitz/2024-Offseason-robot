package frc.robot.subsystems.elevator;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.ControllableMotor;
import frc.robot.hardware.request.IRequest;
import frc.robot.hardware.request.cansparkmax.SparkMaxAngleRequest;
import frc.robot.hardware.request.cansparkmax.SparkMaxDoubleRequest;
import frc.robot.subsystems.GBSubsystem;
import frc.utils.Conversions;
import frc.utils.calibration.sysid.SysIdCalibrator;
import org.littletonrobotics.junction.Logger;

public class Elevator extends GBSubsystem {

	private final DigitalInputInputsAutoLogged digitalInputsInputs;
	private final ElevatorCommandsBuilder commandsBuilder;
	private final IRequest<Rotation2d> positionRequest;
	private final IRequest<Double> voltageRequest;

	private final ElevatorStuff elevatorStuff;
	private final ControllableMotor frontMotor;
	private final ControllableMotor backMotor;
	private final IDigitalInput limitSwitch;
	
	private SysIdCalibrator sysIdCalibrator;

	public Elevator(ElevatorStuff elevatorStuff) {
		super(elevatorStuff.logPath());

		this.frontMotor = elevatorStuff.frontMotorStuff().motor();
		this.backMotor = elevatorStuff.backMotorStuff().motor();
		this.limitSwitch = elevatorStuff.digitalInput();
		this.digitalInputsInputs = new DigitalInputInputsAutoLogged();
		this.elevatorStuff = elevatorStuff;
		this.positionRequest = elevatorStuff.positionRequest();
		this.voltageRequest = elevatorStuff.voltageRequest();
		this.sysIdCalibrator = new SysIdCalibrator(backMotor.getSysidConfigInfo(),this, this::setVoltage);

		frontMotor.resetPosition(metersToMotorRotations(ElevatorConstants.MINIMUM_ACHIEVABLE_POSITION_METERS));
		backMotor.resetPosition(metersToMotorRotations(ElevatorConstants.MINIMUM_ACHIEVABLE_POSITION_METERS));

		this.commandsBuilder = new ElevatorCommandsBuilder(this);
		
		frontMotor.resetPosition(new Rotation2d());
		backMotor.resetPosition(new Rotation2d());
		updateInputs();
	}

	public ElevatorCommandsBuilder getCommandsBuilder() {
		return commandsBuilder;
	}

	protected void setPower(double power) {
		frontMotor.setPower(power);
		backMotor.setPower(power);
	}

	protected void setVoltage(double voltage) {
		frontMotor.applyDoubleRequest(voltageRequest.withSetPoint(voltage));
		backMotor.applyDoubleRequest(voltageRequest.withSetPoint(voltage));
	}

	protected void stop() {
		frontMotor.stop();
		backMotor.stop();
	}

	public void setBrake(boolean brake) {
		frontMotor.setBrake(brake);
		backMotor.setBrake(brake);
	}

	protected void setTargetPositionMeters(double position) {
		Rotation2d angleSetPoint = metersToMotorRotations(position);
		frontMotor.applyAngleRequest(positionRequest.withSetPoint(angleSetPoint));
		backMotor.applyAngleRequest(positionRequest.withSetPoint(angleSetPoint));
	}

	public boolean isAtBackwardLimit() {
		return digitalInputsInputs.debouncedValue;
	}

	private Rotation2d getElevatorAngle() {
		return Rotation2d.fromRotations(
			(elevatorStuff.frontMotorStuff().positionSignal().getLatestValue().getRotations()
				+ elevatorStuff.backMotorStuff().positionSignal().getLatestValue().getRotations()) / 2
		);
	}
	
	public SysIdCalibrator getSysIdCalibrator(){
		return this.sysIdCalibrator;
	}

	public double getPositionMeters() {
		return motorRotationsToMeters(elevatorStuff.frontMotorStuff().positionSignal().getLatestValue());
	}

	protected void stayInPlace() {
		setTargetPositionMeters(getPositionMeters());
	}

	protected void updateInputs() {
		limitSwitch.updateInputs(digitalInputsInputs);
		frontMotor.updateSignals(elevatorStuff.frontMotorStuff().positionSignal(), elevatorStuff.frontMotorStuff().voltageSignal());
		frontMotor.updateSignals(elevatorStuff.frontMotorStuff().signals());
		backMotor.updateSignals(elevatorStuff.backMotorStuff().signals());

		Logger.processInputs(elevatorStuff.digitalInputsLogPath(), digitalInputsInputs);
		Logger.recordOutput(getLogPath() + "isAtBackwardLimit", isAtBackwardLimit());
		Logger.recordOutput(getLogPath() + "elevatorPosition", getPositionMeters());
		
		Logger.recordOutput("elevator position meters", Elevator.motorRotationsToMeters(elevatorStuff.frontMotorStuff().positionSignal().getLatestValue()));
	}

	@Override
	protected void subsystemPeriodic() {
//		if (ElevatorConstants.MINIMUM_ACHIEVABLE_POSITION_METERS > getPositionMeters()) {
//			frontMotor.resetPosition(metersToMotorRotations(ElevatorConstants.MINIMUM_ACHIEVABLE_POSITION_METERS));
//			backMotor.resetPosition(metersToMotorRotations(ElevatorConstants.MINIMUM_ACHIEVABLE_POSITION_METERS));
//		}
		
		Logger.recordOutput("target a ",((SparkMaxAngleRequest) positionRequest).getSetPoint().getRotations());
		Logger.recordOutput("current a ",(elevatorStuff.frontMotorStuff().positionSignal().getLatestValue().getRotations()));
		
		updateInputs();
	}
	
	public static double motorRotationsToMeters(Rotation2d rotations) {
		return Conversions.angleToDistance(rotations,ElevatorConstants.MOTOR_ROTATIONS_TO_METERS_CONVERSION_RATIO);
	}
	
	public static Rotation2d metersToMotorRotations(double meters) {
		Logger.recordOutput(String.valueOf(meters),  Conversions.distanceToAngle(meters ,ElevatorConstants.MOTOR_ROTATIONS_TO_METERS_CONVERSION_RATIO).getRotations());
		return Conversions.distanceToAngle(meters ,ElevatorConstants.MOTOR_ROTATIONS_TO_METERS_CONVERSION_RATIO);
	}

}

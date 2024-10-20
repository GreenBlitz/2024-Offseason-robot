package frc.robot.subsystems.intake.roller;

public enum IntakeStates {

	INTAKE(0.8),
	SHOOTER_TO_ELEVATOR(-0.5),
	OUTTAKE(-0.7),
	STOP(0);

	private final double power;

	IntakeStates(double power) {
		this.power = power;
	}

	public double getPower() {
		return power;
	}

}

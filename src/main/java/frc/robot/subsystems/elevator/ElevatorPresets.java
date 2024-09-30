package frc.robot.subsystems.elevator;

public enum ElevatorPresets {

	// ! should check positions once we have a robot
	SCORE(0),
	AMP(0),
	DEFAULT(0);

	private final double position;

	ElevatorPresets(double position) {
		this.position = position;
	}

	public double getPosition() {
		return position;
	}

}

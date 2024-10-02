package frc.robot.subsystems.chooser;

public enum ChooserState {

	NOTE_TO_SHOOTER(0.5),
	SHOOTER_TO_ELEVATOR(0.5),
	ELEVATOR_TO_SHOOTER(-0.5);

	private final double power;

	ChooserState(double power) {
		this.power = power;
	}

	public double getPower() {
		return power;
	}

}

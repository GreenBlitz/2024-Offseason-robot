package frc.robot.subsystems.elevator;

import edu.wpi.first.math.geometry.Rotation2d;

public enum ElevatorPresets {

	// ! should check positions once we have a robot
	SCORE(Rotation2d.fromRotations(0)),
	AMP(Rotation2d.fromRotations(0)),
	DEFAULT(Rotation2d.fromRotations(0));

	private final Rotation2d position;

	ElevatorPresets(Rotation2d position) {
		this.position = position;
	}

	public Rotation2d getMotorAngle() {
		return position;
	}

}

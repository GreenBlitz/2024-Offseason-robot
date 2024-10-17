package frc.robot.superstructure;

import edu.wpi.first.wpilibj2.command.Command;

public class StatesMotionPlanner {

	private final Superstructure superstructure;

	public StatesMotionPlanner(Superstructure superstructure) {
		this.superstructure = superstructure;
	}

	public Command setState(RobotState state) {
		return switch (state) {
			case INTAKE, PRE_SPEAKER, PRE_AMP, TRANSFER_ELEVATOR_SHOOTER, TRANSFER_SHOOTER_ELEVATOR ->
				superstructure.setState(state)
					.until(superstructure::isEnableChangeStateAutomatically)
					.andThen(superstructure.setState(RobotState.IDLE));
			case INTAKE_OUTTAKE, SPEAKER, AMP, IDLE, SHOOTER_OUTTAKE -> superstructure.setState(state);
		};
	}

}


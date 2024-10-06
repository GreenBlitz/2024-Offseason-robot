package frc.robot.superstructure;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Robot;
import frc.robot.constants.Field;
import frc.robot.subsystems.funnel.FunnelState;
import frc.robot.subsystems.funnel.FunnelStateHandler;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.subsystems.swerve.SwerveState;
import frc.robot.subsystems.swerve.swervestatehelpers.AimAssist;
import org.littletonrobotics.junction.Logger;

public class Superstructure {

	private final Robot robot;
	private final Swerve swerve;
	private final FunnelStateHandler funnelStateHandler;

	private RobotState currentState;

	public Superstructure(Robot robot) {
		this.robot = robot;
		this.swerve = robot.getSwerve();
		this.funnelStateHandler = new FunnelStateHandler(robot);
	}

	public RobotState getCurrentState() {
		return currentState;
	}

	public void logStatus() {
		Logger.recordOutput("CurrentState", currentState);
	}

	private boolean isNoteInShooter() {
		return robot.getFunnel().isNoteInShooter();
	}

	public Command setState(RobotState state) {
		return switch (state) {
			case IDLE -> idle();
			case INTAKE -> intake();
			case PRE_SPEAKER -> preSpeaker();
			case SPEAKER -> speaker();
			case PRE_AMP -> preAmp();
			case AMP -> amp();
			case TRANSFER_SHOOTER_ELEVATOR -> transferShooterElevator();
			case TRANSFER_ELEVATOR_SHOOTER -> transferElevatorShooter();
			case INTAKE_OUTTAKE -> intakeOuttake();
			case SHOOTER_OUTTAKE -> shooterOuttake();
		};
	}

	//@formatter:off
	public Command idle() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
				funnelStateHandler.setState(FunnelState.STOP)
		);
	}

	public Command intake() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.NOTE)),
				funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER)
		);
	}

	public Command preSpeaker() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.SPEAKER)),
				funnelStateHandler.setState(FunnelState.STOP)
		);
	}

	public Command speaker() {
		return new ParallelCommandGroup(
				funnelStateHandler.setState(FunnelState.SPEAKER),
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.SPEAKER))
		);
	}

	public Command preAmp() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.AMP)),
				funnelStateHandler.setState(FunnelState.STOP)
		);
	}

	public Command amp() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.AMP))
						.until(() -> swerve.isAtHeading(Field.getAngleToAmp())),
				funnelStateHandler.setState(FunnelState.AMP)
		);
	}

	public Command transferShooterElevator() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
				funnelStateHandler.setState(FunnelState.SHOOTER_TO_ELEVATOR)
		);
	}

	public Command transferElevatorShooter() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
				funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER)
		);
	}

	public Command intakeOuttake() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
				funnelStateHandler.setState(FunnelState.INTAKE_OUTTAKE)
		);
	}

	public Command shooterOuttake() {
		return new ParallelCommandGroup(
				swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
				funnelStateHandler.setState(FunnelState.SHOOTER_OUTTAKE)
		).until(() -> !isNoteInShooter());
	}
	//@formatter:on

}

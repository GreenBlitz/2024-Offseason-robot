package frc.robot.superstructure;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Robot;
import frc.robot.subsystems.flywheel.FlywheelState;
import frc.robot.subsystems.flywheel.FlywheelStateHandler;
import frc.robot.constants.Field;
import frc.robot.subsystems.elevatorRoller.ElevatorRollerState;
import frc.robot.subsystems.elevatorRoller.ElevatorRollerStateHandler;
import frc.robot.subsystems.funnel.FunnelState;
import frc.robot.subsystems.funnel.FunnelStateHandler;
import frc.robot.subsystems.intake.roller.IntakeStates;
import frc.robot.subsystems.intake.roller.IntakeStatesHandler;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.subsystems.swerve.SwerveState;
import frc.robot.subsystems.swerve.swervestatehelpers.AimAssist;
import org.littletonrobotics.junction.Logger;

public class Superstructure {

	private final Robot robot;
	private final Swerve swerve;
	private final FlywheelStateHandler flywheelStateHandler;
	private final ElevatorRollerStateHandler elevatorRollerStateHandler;
	private final FunnelStateHandler funnelStateHandler;
	private final IntakeStatesHandler intakeStatesHandler;

	private RobotState currentState;

	public Superstructure(Robot robot) {
		this.robot = robot;
		this.swerve = robot.getSwerve();
		this.flywheelStateHandler = new FlywheelStateHandler(robot);
		this.elevatorRollerStateHandler = new ElevatorRollerStateHandler(robot);
		this.funnelStateHandler = new FunnelStateHandler(robot);
		this.intakeStatesHandler = new IntakeStatesHandler(robot.getIntakeRoller());
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

	private boolean isNoteInElevatorRoller() {
		return robot.getElevatorRoller().isNoteIn();
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
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			funnelStateHandler.setState(FunnelState.STOP),
			intakeStatesHandler.setState(IntakeStates.STOP)
		);
	}

	public Command intake() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.NOTE)),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER),
			intakeStatesHandler.setState(IntakeStates.INTAKE)
		);
	}

	public Command preSpeaker() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.SPEAKER)),
			flywheelStateHandler.setState(FlywheelState.SHOOTING),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			funnelStateHandler.setState(FunnelState.STOP),
			intakeStatesHandler.setState(IntakeStates.STOP)
		);
	}

	public Command speaker() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.SPEAKER)),
			flywheelStateHandler.setState(FlywheelState.SHOOTING),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			funnelStateHandler.setState(FunnelState.SPEAKER) ,
			intakeStatesHandler.setState(IntakeStates.STOP)
		);
	}

	public Command preAmp() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.AMP)),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_TO_ELEVATOR),
			funnelStateHandler.setState(FunnelState.STOP)
		).until(this::isNoteInElevatorRoller);
	}

	public Command amp() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.AMP)).until(() -> swerve.isAtHeading(Field.getAngleToAmp())),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.AMP),
			funnelStateHandler.setState(FunnelState.AMP)
		);
	}

	public Command transferShooterElevator() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_TO_ELEVATOR),
			funnelStateHandler.setState(FunnelState.SHOOTER_TO_ELEVATOR),
			intakeStatesHandler.setState(IntakeStates.SHOOTER_TO_ELEVATOR)
		);
	}

	public Command transferElevatorShooter() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_FROM_ELEVATOR),
			funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER),
			intakeStatesHandler.setState(IntakeStates.NOTE_TO_SHOOTER)
		);
	}

	public Command intakeOuttake() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_FROM_ELEVATOR),
			funnelStateHandler.setState(FunnelState.INTAKE_OUTTAKE),
			intakeStatesHandler.setState(IntakeStates.OUTTAKE)
		);
	}

	public Command shooterOuttake() {
		return new ParallelCommandGroup(
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			flywheelStateHandler.setState(FlywheelState.SHOOTER_OUTTAKE),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			funnelStateHandler.setState(FunnelState.SHOOTER_OUTTAKE),
			intakeStatesHandler.setState(IntakeStates.STOP)
		).until(() -> !isNoteInShooter());
	}
	//@formatter:on

}

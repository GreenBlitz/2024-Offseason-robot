package frc.robot.superstructure;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
import frc.robot.subsystems.elevator.ElevatorStates;
import frc.robot.subsystems.elevator.ElevatorStatesHandler;
import frc.robot.subsystems.elevatorRoller.ElevatorRollerState;
import frc.robot.subsystems.elevatorRoller.ElevatorRollerStateHandler;
import frc.robot.subsystems.flywheel.FlywheelState;
import frc.robot.subsystems.flywheel.FlywheelStateHandler;
import frc.robot.subsystems.funnel.FunnelState;
import frc.robot.subsystems.funnel.FunnelStateHandler;
import frc.robot.subsystems.intake.pivot.PivotState;
import frc.robot.subsystems.intake.pivot.PivotStateHandler;
import frc.robot.subsystems.intake.roller.IntakeStates;
import frc.robot.subsystems.intake.roller.IntakeStatesHandler;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.subsystems.swerve.SwerveState;
import frc.robot.subsystems.swerve.swervestatehelpers.AimAssist;
import frc.utils.time.TimeUtils;
import org.littletonrobotics.junction.Logger;
import java.util.concurrent.atomic.AtomicReference;

public class Superstructure {

	private final String logPath;

	private final Robot robot;
	private final Swerve swerve;
	private final ElevatorRollerStateHandler elevatorRollerStateHandler;
	private final FlywheelStateHandler flywheelStateHandler;
	private final FunnelStateHandler funnelStateHandler;
	private final IntakeStatesHandler intakeStatesHandler;
	private final PivotStateHandler pivotStateHandler;
	private final ElevatorStatesHandler elevatorStatesHandler;

	private RobotState currentState;

	private boolean enableChangeStateAutomatically;

	public Superstructure(String logPath, Robot robot) {
		this.logPath = logPath;

		this.robot = robot;
		this.swerve = robot.getSwerve();
		this.elevatorRollerStateHandler = new ElevatorRollerStateHandler(robot.getElevatorRoller());
		this.flywheelStateHandler = new FlywheelStateHandler(robot.getFlywheel());
		this.funnelStateHandler = new FunnelStateHandler(robot.getFunnel());
		this.intakeStatesHandler = new IntakeStatesHandler(robot.getIntakeRoller());
		this.pivotStateHandler = new PivotStateHandler(robot.getPivot());
		this.elevatorStatesHandler = new ElevatorStatesHandler(robot.getElevator());

		this.enableChangeStateAutomatically = true;
	}

	public RobotState getCurrentState() {
		return currentState;
	}

	public void logStatus() {
		Logger.recordOutput(logPath + "CurrentState", currentState);
		Logger.recordOutput(logPath + "EnableChangeStateAutomatically", enableChangeStateAutomatically);
	}

	public boolean isEnableChangeStateAutomatically() {
		return enableChangeStateAutomatically;
	}

	private boolean isNoteInShooter() {
		return robot.getFunnel().isNoteInShooter();
	}

	private boolean isNoteInElevatorRoller() {
		return robot.getElevatorRoller().isNoteIn();
	}

	private boolean isNoteInIntake() {
		return robot.getIntakeRoller().isNoteIn();
	}

	private boolean isReadyToShoot() {
		boolean isFlywheelReady = robot.getFlywheel().isAtVelocity(FlywheelState.SHOOTING.getVelocity(), Tolerances.FLYWHEEL_VELOCITY_TOLERANCE);
		// boolean isSwerveReady = swerve.isAtHeading(speaker);
		return isFlywheelReady && isNoteInShooter(); // && isSwerveReady
	}

	private boolean isReadyToAmp() {
		boolean isElevatorRollerReady = robot.getElevatorRoller().isNoteIn();
		boolean isElevatorReady = MathUtil.isNear(
			ElevatorStates.AMP.getPositionMeters(),
			robot.getElevator().getPositionMeters(),
			Tolerances.ELEVATOR_POSITION_METERS_TOLERANCE
		);
		return isElevatorRollerReady && isElevatorReady;
	}

	private boolean isNoteInRobot(AtomicReference<Double> lastTimeDetectedNote) {
		if (!isNoteInIntake() && !isNoteInElevatorRoller() && !isNoteInShooter()) {
			boolean noteNotDetectedInAWhile = lastTimeDetectedNote.get() - TimeUtils.getCurrentTimeSeconds()
				> Tolerances.OUT_OF_ROBOT_NOTE_DETECTION_TIMEOUT_SECONDS;
			return noteNotDetectedInAWhile;
		} else {
			lastTimeDetectedNote.set(TimeUtils.getCurrentTimeSeconds());
		}
		return false;
	}

	public Command enableChangeStateAutomatically(boolean enable) {
		return new InstantCommand(() -> enableChangeStateAutomatically = enable);
	}

	private Command setCurrentStateValue(RobotState state) {
		return new InstantCommand(() -> currentState = state);
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
			enableChangeStateAutomatically(true),
			setCurrentStateValue(RobotState.IDLE),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			funnelStateHandler.setState(FunnelState.STOP),
			intakeStatesHandler.setState(IntakeStates.STOP),
			pivotStateHandler.setState(PivotState.UP),
			elevatorStatesHandler.setState(ElevatorStates.IDLE)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command intake() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.INTAKE),
			enableChangeStateAutomatically(false),
			new SequentialCommandGroup(
				new ParallelCommandGroup(
					funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER),
					intakeStatesHandler.setState(IntakeStates.INTAKE),
					pivotStateHandler.setState(PivotState.ON_FLOOR)
				).until(this::isNoteInIntake),
				new ParallelCommandGroup(
					funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER),
					intakeStatesHandler.setState(IntakeStates.INTAKE),
					pivotStateHandler.setState(PivotState.UP)
				).until(this::isNoteInShooter),
					enableChangeStateAutomatically(true),
				new ParallelCommandGroup(
					funnelStateHandler.setState(FunnelState.STOP),
					intakeStatesHandler.setState(IntakeStates.STOP)
				)
			),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.NOTE)),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorStatesHandler.setState(ElevatorStates.IDLE)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command preSpeaker() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.PRE_SPEAKER),
			enableChangeStateAutomatically(true),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.SPEAKER)),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			flywheelStateHandler.setState(FlywheelState.SHOOTING),
			funnelStateHandler.setState(FunnelState.STOP),
			intakeStatesHandler.setState(IntakeStates.STOP),
			pivotStateHandler.setState(PivotState.UP),
			elevatorStatesHandler.setState(ElevatorStates.IDLE)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command speaker() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.SPEAKER),
			enableChangeStateAutomatically(false),
			new SequentialCommandGroup(
				funnelStateHandler.setState(FunnelState.STOP).until(this::isReadyToShoot),
				funnelStateHandler.setState(FunnelState.SPEAKER).until(() -> !isNoteInShooter()),
				funnelStateHandler.setState(FunnelState.STOP)
			),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.SPEAKER)),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			flywheelStateHandler.setState(FlywheelState.SHOOTING),
			intakeStatesHandler.setState(IntakeStates.STOP),
			pivotStateHandler.setState(PivotState.UP),
			elevatorStatesHandler.setState(ElevatorStates.IDLE)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command preAmp() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.PRE_AMP),
			enableChangeStateAutomatically(false),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.AMP)),
			funnelStateHandler.setState(FunnelState.STOP),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			intakeStatesHandler.setState(IntakeStates.STOP),
			pivotStateHandler.setState(PivotState.UP),
			elevatorStatesHandler.setState(ElevatorStates.PRE_AMP)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command amp() {
		return new ParallelCommandGroup(
			enableChangeStateAutomatically(false),
			setCurrentStateValue(RobotState.AMP),
			new SequentialCommandGroup(
				new ParallelCommandGroup(
					elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
					swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE.withAimAssist(AimAssist.AMP)),
					elevatorStatesHandler.setState(ElevatorStates.PRE_AMP)
				).until(this::isReadyToAmp),
				new ParallelCommandGroup(
					elevatorRollerStateHandler.setState(ElevatorRollerState.AMP)
				).until(() -> !isNoteInElevatorRoller()),
				new ParallelCommandGroup(
					elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
					elevatorStatesHandler.setState(ElevatorStates.IDLE)
				)
			),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			intakeStatesHandler.setState(IntakeStates.STOP),
			pivotStateHandler.setState(PivotState.UP),
			funnelStateHandler.setState(FunnelState.STOP)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command transferShooterElevator() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.TRANSFER_SHOOTER_ELEVATOR),
			enableChangeStateAutomatically(false),
			new SequentialCommandGroup(
				new ParallelCommandGroup(
					intakeStatesHandler.setState(IntakeStates.SHOOTER_TO_ELEVATOR),
					funnelStateHandler.setState(FunnelState.SHOOTER_TO_ELEVATOR)
				).until(this::isNoteInElevatorRoller),
				new ParallelCommandGroup(
					intakeStatesHandler.setState(IntakeStates.STOP),
					funnelStateHandler.setState(FunnelState.STOP)
				)
			),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			new SequentialCommandGroup(
				elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_TO_ELEVATOR).until(this::isNoteInElevatorRoller),
				elevatorRollerStateHandler.setState(ElevatorRollerState.STOP)
			),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorStatesHandler.setState(ElevatorStates.IDLE),
			pivotStateHandler.setState(PivotState.UP)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command transferElevatorShooter() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.TRANSFER_ELEVATOR_SHOOTER),
			enableChangeStateAutomatically(false),
			new SequentialCommandGroup(
				new ParallelCommandGroup(
					intakeStatesHandler.setState(IntakeStates.ELEVATOR_TO_SHOOTER),
					funnelStateHandler.setState(FunnelState.NOTE_TO_SHOOTER)
				).until(this::isNoteInShooter),
				new ParallelCommandGroup(
					intakeStatesHandler.setState(IntakeStates.STOP),
					funnelStateHandler.setState(FunnelState.STOP)
				)
			),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			new SequentialCommandGroup(
				elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_FROM_ELEVATOR).until(() -> !this.isNoteInElevatorRoller()),
				elevatorRollerStateHandler.setState(ElevatorRollerState.STOP)
			),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			elevatorStatesHandler.setState(ElevatorStates.IDLE),
			pivotStateHandler.setState(PivotState.UP)
		).handleInterrupt(() -> enableChangeStateAutomatically(true).schedule());
	}

	public Command intakeOuttake() {
		AtomicReference<Double> lastTimeDetectedNote = new AtomicReference<>(TimeUtils.getCurrentTimeSeconds());
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.INTAKE_OUTTAKE),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			elevatorRollerStateHandler.setState(ElevatorRollerState.TRANSFER_FROM_ELEVATOR),
			flywheelStateHandler.setState(FlywheelState.DEFAULT),
			funnelStateHandler.setState(FunnelState.INTAKE_OUTTAKE),
			intakeStatesHandler.setState(IntakeStates.OUTTAKE),
			pivotStateHandler.setState(PivotState.ON_FLOOR),
			elevatorStatesHandler.setState(ElevatorStates.IDLE)
		).until(() -> this.isNoteInRobot(lastTimeDetectedNote));
	}

	public Command shooterOuttake() {
		return new ParallelCommandGroup(
			setCurrentStateValue(RobotState.SHOOTER_OUTTAKE),
			new SequentialCommandGroup(
				funnelStateHandler.setState(FunnelState.SHOOTER_OUTTAKE).until(() -> !isNoteInShooter()),
				funnelStateHandler.setState(FunnelState.STOP)
			),
			swerve.getCommandsBuilder().saveState(SwerveState.DEFAULT_DRIVE),
			elevatorRollerStateHandler.setState(ElevatorRollerState.STOP),
			flywheelStateHandler.setState(FlywheelState.SHOOTER_OUTTAKE),
			intakeStatesHandler.setState(IntakeStates.STOP),
			pivotStateHandler.setState(PivotState.UP),
			elevatorStatesHandler.setState(ElevatorStates.IDLE)
		).until(() -> !isNoteInShooter());
	}
	//@formatter:on

}

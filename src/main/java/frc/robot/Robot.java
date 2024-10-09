// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.constants.IDs;
import frc.robot.subsystems.flywheel.Flywheel;
import frc.robot.subsystems.flywheel.FlywheelComponents;
import frc.robot.subsystems.flywheel.FlywheelConstants;
import frc.robot.subsystems.flywheel.factory.FlywheelFactory;
import frc.robot.subsystems.elevatorRoller.ElevatorRoller;
import frc.robot.subsystems.elevatorRoller.factory.ElevatorRollerConstants;
import frc.robot.subsystems.elevatorRoller.factory.ElevatorRollerFactory;
import frc.robot.subsystems.intake.roller.IntakeRoller;
import frc.robot.subsystems.intake.roller.IntakeRollerConstant;
import frc.robot.subsystems.intake.roller.factory.IntakeRollerFactory;
import frc.robot.subsystems.intake.pivot.Pivot;
import frc.robot.subsystems.intake.pivot.PivotConstants;
import frc.robot.subsystems.intake.pivot.factory.PivotFactory;
import frc.robot.subsystems.intake.roller.IntakeRoller;
import frc.robot.subsystems.intake.roller.IntakeRollerConstant;
import frc.robot.subsystems.intake.roller.factory.IntakeRollerFactory;
import frc.robot.subsystems.swerve.Swerve;
import frc.robot.subsystems.swerve.SwerveType;
import frc.robot.subsystems.swerve.factories.gyro.GyroFactory;
import frc.robot.subsystems.swerve.factories.modules.ModulesFactory;
import frc.robot.subsystems.swerve.factories.swerveconstants.SwerveConstantsFactory;
import frc.robot.subsystems.funnel.Funnel;
import frc.robot.subsystems.funnel.FunnelConstants;
import frc.robot.subsystems.funnel.factory.FunnelFactory;
import frc.robot.superstructure.Superstructure;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link RobotManager} periodic methods (other than the scheduler calls). Instead, the structure of the robot
 * (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class Robot {

	public static final RobotType ROBOT_TYPE = RobotType.determineRobotType();

	private final Swerve swerve;

	private final ElevatorRoller elevatorRoller;

	private final Funnel funnel;
	private final Pivot pivot;

	private final IntakeRoller intakeRoller;

	private final Flywheel flywheel;

	private final Superstructure superstructure;

	public Robot() {
		FlywheelComponents topFlywheelComponents = FlywheelFactory
			.create(FlywheelConstants.LOG_PATH + "TopMotor", true, IDs.CANSparkMAXIDs.TOP_FLYWHEEL);

		FlywheelComponents bottomFlywheelComponents = FlywheelFactory
			.create(FlywheelConstants.LOG_PATH + "BottomMotor", false, IDs.CANSparkMAXIDs.BOTTOM_FLYWHEEL);

		this.flywheel = new Flywheel(topFlywheelComponents, bottomFlywheelComponents, FlywheelConstants.LOG_PATH, this);
		this.swerve = new Swerve(
			SwerveConstantsFactory.create(SwerveType.SWERVE),
			ModulesFactory.create(SwerveType.SWERVE),
			GyroFactory.create(SwerveType.SWERVE)
		);
		this.elevatorRoller = new ElevatorRoller(ElevatorRollerFactory.create(ElevatorRollerConstants.LOG_PATH));
		this.funnel = new Funnel(FunnelFactory.create(FunnelConstants.LOG_PATH));
		this.pivot = new Pivot(PivotFactory.create(PivotConstants.LOG_PATH));
		this.intakeRoller = new IntakeRoller(IntakeRollerFactory.create(IntakeRollerConstant.LOG_PATH));
		this.superstructure = new Superstructure(this);

		configureBindings();
	}

	private void configureBindings() {
		JoysticksBindings.configureBindings(this);
	}

	public Command getAutonomousCommand() {
		return new InstantCommand();
	}

	public Swerve getSwerve() {
		return swerve;
	}

	public ElevatorRoller getElevatorRoller() {
		return elevatorRoller;
	}

	public Funnel getFunnel() {
		return funnel;
	}

	public Pivot getPivot() {
		return pivot;
	}

	public Flywheel getFlywheel() {
		return flywheel;
	}

	public IntakeRoller getIntakeRoller() {
		return intakeRoller;
	}

	public Superstructure getSuperstructure() {
		return superstructure;
	}

}

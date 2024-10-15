package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.utils.calibration.sysid.SysIdCalibrator;
import frc.utils.joysticks.Axis;
import frc.utils.joysticks.JoystickPorts;
import frc.utils.joysticks.SmartJoystick;

public class JoysticksBindings {

	private static final SmartJoystick MAIN_JOYSTICK = new SmartJoystick(JoystickPorts.MAIN);
	private static final SmartJoystick SECOND_JOYSTICK = new SmartJoystick(JoystickPorts.SECOND);
	private static final SmartJoystick THIRD_JOYSTICK = new SmartJoystick(JoystickPorts.THIRD);
	private static final SmartJoystick FOURTH_JOYSTICK = new SmartJoystick(JoystickPorts.FOURTH);
	private static final SmartJoystick FIFTH_JOYSTICK = new SmartJoystick(JoystickPorts.FIFTH);
	private static final SmartJoystick SIXTH_JOYSTICK = new SmartJoystick(JoystickPorts.SIXTH);

	public static void configureBindings(Robot robot) {
		mainJoystickButtons(robot);
		secondJoystickButtons(robot);
		thirdJoystickButtons(robot);
		fourthJoystickButtons(robot);
		fifthJoystickButtons(robot);
		sixthJoystickButtons(robot);
	}

	private static void mainJoystickButtons(Robot robot) {
		SmartJoystick usedJoystick = MAIN_JOYSTICK;
		// bindings...
//
//		robot.getSwerve()
//			.setDefaultCommand(
//				robot.getSwerve()
//					.getCommandsBuilder()
//					.driveBySavedState(
//						() -> usedJoystick.getAxisValue(Axis.LEFT_Y),
//						() -> usedJoystick.getAxisValue(Axis.LEFT_X),
//						() -> usedJoystick.getAxisValue(Axis.RIGHT_X)
//					)
//			);
	}

	private static void secondJoystickButtons(Robot robot) {
		SmartJoystick usedJoystick = SECOND_JOYSTICK;
		// bindings...
		
		usedJoystick.A.whileTrue(robot.getPivot().getCommandsBuilder().goToPosition(Rotation2d.fromDegrees(15)));
		usedJoystick.B.whileTrue(robot.getPivot().getCommandsBuilder().goToPosition(Rotation2d.fromDegrees(45)));
		usedJoystick.Y.whileTrue(robot.getPivot().getCommandsBuilder().goToPosition(Rotation2d.fromDegrees(100)));
		usedJoystick.X.whileTrue(robot.getPivot().getCommandsBuilder().stop());
	}

	private static void thirdJoystickButtons(Robot robot) {
		SmartJoystick usedJoystick = THIRD_JOYSTICK;
		// bindings...
	}

	private static void fourthJoystickButtons(Robot robot) {
		SmartJoystick usedJoystick = FOURTH_JOYSTICK;
		// bindings...
	}

	private static void fifthJoystickButtons(Robot robot) {
		SmartJoystick usedJoystick = FIFTH_JOYSTICK;
		// bindings...
	}

	private static void sixthJoystickButtons(Robot robot) {
		SmartJoystick usedJoystick = SIXTH_JOYSTICK;
		// bindings...
	}

}

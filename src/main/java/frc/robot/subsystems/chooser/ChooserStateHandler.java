package frc.robot.subsystems.chooser;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class ChooserStateHandler {

	private final Chooser chooser;

	public ChooserStateHandler(Robot robot) {
		this.chooser = robot.getChooser();
	}

	public Command setState(ChooserState state) {
		return chooser.getCommandsBuilder().setPower(state.getPower());
	}

}

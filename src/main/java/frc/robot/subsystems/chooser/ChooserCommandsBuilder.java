package frc.robot.subsystems.chooser;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;

import java.util.function.DoubleSupplier;

public class ChooserCommandsBuilder {

	private final Chooser chooser;

	public ChooserCommandsBuilder(Chooser chooser) {
		this.chooser = chooser;
	}

	//@formatter:off
	public Command setPower(double power) {
        return new FunctionalCommand(
				() -> {},
				() -> chooser.setPower(power),
				interrupted -> chooser.stop(),
				() -> false,
				chooser
		).withName("Set power: " + power);
    }

    public Command setPower(DoubleSupplier power) {
        return new FunctionalCommand(
				() -> {},
				() -> chooser.setPower(power.getAsDouble()),
				interrupted -> chooser.stop(),
				() -> false,
				chooser
		).withName("Set power by supplier");
    }
	//@formatter:on

	public Command stop() {
		return new RunCommand(chooser::stop, chooser).withName("Stop");
	}

}

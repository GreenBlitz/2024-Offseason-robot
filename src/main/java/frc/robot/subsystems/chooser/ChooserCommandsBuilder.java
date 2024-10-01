package frc.robot.subsystems.chooser;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

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
		).withName("Set chooser power: " + power);
    }

    public Command setPower(DoubleSupplier power) {
        return new FunctionalCommand(
				() -> {},
				() -> chooser.setPower(power.getAsDouble()),
				interrupted -> chooser.stop(),
				() -> false,
				chooser
		).withName("Set chooser power by supplier");
    }
	//@formatter:on

	public Command stop() {
		return new InstantCommand(chooser::stop, chooser).withName("Stop chooser");
	}

}

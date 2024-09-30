package frc.robot.subsystems.funnel;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import java.util.function.DoubleSupplier;

public class FunnelCommandsBuilder {

    private final Funnel funnel;

    public FunnelCommandsBuilder(Funnel funnel) {
        this.funnel = funnel;
    }

    //@formatter:off
	public Command setBigFunnelPower(double power) {
		return new FunctionalCommand(
				() -> funnel.setPower(power),
				() -> {},
				interrupted -> funnel.stop(),
				() -> false,
				funnel
		).withName("Set funnel power: " + power);
	}

    public Command setBigFunnelPower(DoubleSupplier power) {
        return new FunctionalCommand(
                () -> {
                },
                () -> funnel.setPower(power.getAsDouble()),
                interrupted -> funnel.stop(),
                () -> false,
                funnel
        ).withName("Set funnel power by supplier");
    }

    public Command rollFunnel(Rotation2d rotations, double power) {
        return new FunctionalCommand(
                () -> {
                },
                () -> funnel.setPower(power),
                interrupted -> funnel.stop(),
                () -> funnel.isAtPosition(rotations),
                funnel
        ).withName("Rotate funnel rotations: " + rotations);
    }

    //@formatter:on

    public Command stopBigFunnel() {
        return new InstantCommand(funnel::stop, funnel).withName("Stop funnel");
    }

}

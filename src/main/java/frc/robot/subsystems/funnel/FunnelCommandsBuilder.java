package frc.robot.subsystems.funnel;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import java.util.function.DoubleSupplier;

public class FunnelCommandsBuilder {

    private final Funnel funnel;

    public FunnelCommandsBuilder(Funnel funnel){
        this.funnel = funnel;
    }

    public Command setBigFunnelPower(double power){
        return new FunctionalCommand(
                () -> funnel.setBigFunnelPower(power),
                () -> {},
                interrupted -> funnel.stopBigFunnel(),
                () -> false,
                funnel
        ).withName("Set big funnel power: " + power);
    }

    public Command setMiddleFunnelPower(double power){
        return new FunctionalCommand(
                () -> funnel.setMiddleFunnelPower(power),
                () -> {},
                interrupted -> funnel.stopMiddleFunnel(),
                () -> false,
                funnel
        ).withName("Set middle funnel power: " + power);
    }

    public Command setBigFunnelPower(DoubleSupplier power) {
        return new FunctionalCommand(
                () -> {},
                () -> funnel.setBigFunnelPower(power.getAsDouble()),
                interrupted -> funnel.stopBigFunnel(),
                () -> false,
                funnel
        ).withName("Set big funnel power by supplier");
    }

    public Command setMiddleFunnelPower(DoubleSupplier power) {
        return new FunctionalCommand(
                () -> {},
                () -> funnel.setMiddleFunnelPower(power.getAsDouble()),
                interrupted -> funnel.stopMiddleFunnel(),
                () -> false,
                funnel
        ).withName("Set middle funnel power by supplier");
    }


    public Command rollBigFunnelRotations(Rotation2d rotations, double power) {
        Rotation2d startingPosition = funnel.getBigFunnelPosition();
        return new FunctionalCommand(
                ()-> {},
                ()-> funnel.setBigFunnelPower(power),
                interrupted -> funnel.stopBigFunnel(),
                () -> Math.abs(funnel.getBigFunnelPosition().getRotations() - startingPosition.getRotations()) > rotations.getRotations(),
                funnel
        ).withName("Rotate big funnel rotations: " + rotations);
    }

    public Command rollMiddleFunnelRotations(Rotation2d rotations, double power) {
        Rotation2d startingPosition = funnel.getMiddleFunnelPosition();
        return new FunctionalCommand(
                ()-> {},
                ()-> funnel.setMiddleFunnelPower(power),
                interrupted -> funnel.stopMiddleFunnel(),
                () -> Math.abs(funnel.getMiddleFunnelPosition().getRotations() - startingPosition.getRotations()) > rotations.getRotations(),
                funnel
        ).withName("Rotate middle funnel rotations: " + rotations);
    }

    public Command stopBigFunnel() {
        return new InstantCommand(funnel::stopBigFunnel, funnel).withName("Stop big funnel");
    }

    public Command stopMiddleFunnel() {
        return new InstantCommand(funnel::stopMiddleFunnel, funnel).withName("Stop middle funnel");
    }

}

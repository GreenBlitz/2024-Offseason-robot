package frc.robot.hardware.flywheel;

import com.ctre.phoenix.Util;
import com.revrobotics.CANSparkBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class FlywheelCommandBuilder {

    private Flywheel flywheel;

    public FlywheelCommandBuilder (Flywheel flywheel){
        this.flywheel=flywheel;
    }

    public Command setPower(double power){
        double appliedPower = MathUtil.clamp(power, FlywheelConstants.MIN_BATTARY_POWER, FlywheelConstants.MAX_BATTARY_POWER);
        return new FunctionalCommand(
                () -> flywheel.setPower(appliedPower),
                () -> {} ,
                interrupted -> flywheel.stop(),
                () -> false,
                flywheel
        ).withName("Set Flywheel power to "+power);
    }

    public Command setVelocity(double velocityRPS){
        return new FunctionalCommand(
                () -> flywheel.setPower(),
                () -> {
                    flywheel
                            .setReference(
                                    targetPosition.getRotations(),
                                    CANSparkBase.ControlType.kPosition,
                                    0,
                                    NeoElbowConstants.ARM_FEEDFORWARD_CONTROLLER.calculate(getPosition().getRadians(), getVelocity().getRotations())
                            );
                } ,
                interrupted -> flywheel.stop(),
                () -> false,
                flywheel
        ).withName("Set Flywheel power to "+power);
    }


    public Command stop(){
        return new InstantCommand(flywheel::stop, flywheel);
    }

}

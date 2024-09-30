package frc.robot.subsystems.chooser;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.signal.InputSignal;

public record ChooserAvatiach(
        String logPath,
        String digitalInputLogPath,
        IMotor motor,
        InputSignal<Double> voltageSignal,
        InputSignal<Rotation2d> positionSignal,
        IDigitalInput digitalInput
) {
    public ChooserAvatiach(
            String logPath,
            IMotor motor,
            InputSignal<Double> voltageSignal,
            InputSignal<Rotation2d> positionSignal,
            IDigitalInput digitalInput
    ) {
        this(
                logPath,
                logPath + "digitalInput",
                motor,
                voltageSignal,
                positionSignal,
                digitalInput
        );
    }
}

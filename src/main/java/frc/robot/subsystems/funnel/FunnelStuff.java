package frc.robot.subsystems.funnel;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.robot.hardware.signal.InputSignal;

public record FunnelStuff(
        String logPath,
        String digitalInputLogPath,
        IMotor bigFunnelMotor,
        IMotor middleFunnelMotor,
        InputSignal<Double> bigFunnelVoltageSignal,
        InputSignal<Rotation2d> bigFunnelPositionSignal,
        InputSignal<Double> middleFunnelVoltageSignal,
        InputSignal<Rotation2d> middleFunnelPositionSignal,
        IDigitalInput digitalInput
){
    public FunnelStuff(
            String logPath,
            IMotor bigFunnelMotor,
            IMotor middleFunnelMotor,
            InputSignal<Double> bigFunnelVoltageSignal,
            InputSignal<Rotation2d> bigFunnelPositionSignal,
            InputSignal<Double> middleFunnelVoltageSignal,
            InputSignal<Rotation2d> middleFunnelPositionSignal,
            IDigitalInput digitalInput
    ) {
        this(logPath, logPath + "digitalInput", bigFunnelMotor, middleFunnelMotor, bigFunnelVoltageSignal, bigFunnelPositionSignal, middleFunnelVoltageSignal, middleFunnelPositionSignal, digitalInput);
    }

}
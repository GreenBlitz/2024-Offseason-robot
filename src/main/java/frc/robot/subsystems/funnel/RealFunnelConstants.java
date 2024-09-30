package frc.robot.subsystems.funnel;

import edu.wpi.first.math.filter.Debouncer;
import frc.robot.constants.IDs;
import frc.robot.hardware.motor.sparkmax.SparkMaxWrapper;
import frc.robot.hardware.signal.cansparkmax.SparkMaxDoubleSignal;

import java.util.function.Supplier;

public class RealFunnelConstants {
    public static RollerStuff generateIntakeStuff(String logPath) {
        SparkMaxWrapper sparkMaxWrapper = new SparkMaxWrapper(IDs.CANSparkMAXs.ROLLER);
        sparkMaxWrapper.getEncoder().setPositionConversionFactor(GEAR_RATIO);
        sparkMaxWrapper.getEncoder().setVelocityConversionFactor(GEAR_RATIO);
        SysIdRoutine.Config config = new SysIdRoutine.Config();
        BrushlessSparkMAXMotor motor = new BrushlessSparkMAXMotor(logPath, sparkMaxWrapper, config);

        Supplier<Double> voltage = () -> (sparkMaxWrapper.getBusVoltage() * sparkMaxWrapper.getAppliedOutput());
        SparkMaxDoubleSignal voltageSignal = new SparkMaxDoubleSignal("voltage", voltage);

        Supplier<Double> position = () -> sparkMaxWrapper.getEncoder().getPosition();
        SparkMaxAngleSignal angleSignal = new SparkMaxAngleSignal("position", position, AngleUnit.ROTATIONS);

        BooleanSupplier isBeamBroke = () -> sparkMaxWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).isPressed();
        sparkMaxWrapper.getReverseLimitSwitch(REVERSE_LIMIT_SWITCH_TYPE).enableLimitSwitch(false);

        SuppliedDigitalInput beamBreaker = new SuppliedDigitalInput(isBeamBroke, DEBOUNCE_TYPE, DEBOUNCE_TIME_SECONDS);

        return new RollerStuff(logPath, motor, voltageSignal, angleSignal, beamBreaker);
    }

    private final static double DEBOUNCE_TIME_SECONDS = 0.05;
    private final static Debouncer.DebounceType DEBOUNCE_TYPE = Debouncer.DebounceType.kBoth;

    public static FunnelStuff generateFunnelStuff(String logPath){
        SparkMaxWrapper bigFunnelMotor = new SparkMaxWrapper(IDs.BIG_FUNNEL);
        SparkMaxWrapper middleFunnelMotor = new SparkMaxWrapper(IDs.MIDDLE_FUNNEL);

        Supplier<Double> bigFunnelVoltage = () -> bigFunnelMotor.getBusVoltage() * bigFunnelMotor.getAppliedOutput();
        SparkMaxDoubleSignal bigFunnelVoltageSignal = new SparkMaxDoubleSignal("bigFunnelVoltage", bigFunnelVoltage);
        Supplier<Double> middleFunnelVoltage = () -> middleFunnelMotor.getBusVoltage() * middleFunnelMotor.getAppliedOutput();
        SparkMaxDoubleSignal middleFunnelVoltageSignal = new SparkMaxDoubleSignal("bigFunnelVoltage", middleFunnelVoltage);

        Supplier<Double> bigFunnelPosition = () -> bigFunnelMotor.getEncoder().getPosition();

    }

}

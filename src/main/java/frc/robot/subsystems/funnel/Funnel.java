package frc.robot.subsystems.funnel;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Funnel extends GBSubsystem {

    private final IMotor bigFunnelMotor;
    private final IMotor middleFunnelMotor;
    private final FunnelStuff funnelStuff;
    private final IDigitalInput digitalInput;
    private final DigitalInputInputsAutoLogged digitalInputInputs;
    private Rotation2d bigFunnelTargetPosition;
    private Rotation2d middleFunnelTargetPosition;

    public Funnel(FunnelStuff funnelStuff) {
        super(funnelStuff.logPath());
        this.bigFunnelMotor = funnelStuff.bigFunnelMotor();
        this.middleFunnelMotor = funnelStuff.middleFunnelMotor();
        this.digitalInput = funnelStuff.digitalInput();
        this.funnelStuff = funnelStuff;
        this.digitalInputInputs = new DigitalInputInputsAutoLogged();

        this.bigFunnelTargetPosition = new Rotation2d();
        this.middleFunnelTargetPosition = new Rotation2d();
        update();
    }

    public boolean isObjectIn(){
        return digitalInputInputs.debouncedValue;
    }

    public void update(){
        digitalInput.updateInputs(digitalInputInputs);
        bigFunnelMotor.updateSignals(funnelStuff.bigFunnelVoltageSignal(), funnelStuff.bigFunnelPositionSignal());
        middleFunnelMotor.updateSignals(funnelStuff.middleFunnelVoltageSignal(), funnelStuff.middleFunnelPositionSignal());
    }

    public void setBigFunnelPower(double power){
        bigFunnelMotor.setPower(power);
    }

    public void setMiddleFunnelPower(double power){
        middleFunnelMotor.setPower(power);
    }

    public void stopBigFunnel(){
        bigFunnelMotor.stop();
    }

    public void stopMiddleFunnel(){
        middleFunnelMotor.stop();
    }

    public void setBigFunnelBrake(boolean brake){
        bigFunnelMotor.setBrake(brake);
    }

    public void setMiddleFunnelBrake(boolean brake){
        middleFunnelMotor.setBrake(brake);
    }

    public Rotation2d getBigFunnelPosition(){
        return funnelStuff.bigFunnelPositionSignal().getLatestValue();
    }

    public Rotation2d getMiddleFunnelPosition(){
        return funnelStuff.middleFunnelPositionSignal().getLatestValue();
    }

    public void setBigFunnelTargetPosition(double rotations) {
        this.bigFunnelTargetPosition = Rotation2d.fromRotations(getBigFunnelPosition().getRotations() + rotations);
    }

    public void setMiddleFunnelTargetPosition(double rotations) {
        this.middleFunnelTargetPosition = Rotation2d.fromRotations(getMiddleFunnelPosition().getRotations() + rotations);
    }

    public boolean isBigFunnelPastPosition() {
        return getBigFunnelPosition().getRotations() > bigFunnelTargetPosition.getRotations();
    }

    public boolean isMiddleFunnelPastPosition() {
        return getMiddleFunnelPosition().getRotations() > middleFunnelTargetPosition.getRotations();
    }

    @Override
    protected void subsystemPeriodic() {
        update();
        Logger.processInputs(funnelStuff.digitalInputLogPath(), digitalInputInputs);
        Logger.recordOutput(funnelStuff.logPath() + "IsObjectIn", isObjectIn());
    }

}

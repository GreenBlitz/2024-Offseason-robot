package frc.robot.subsystems.funnel;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Funnel extends GBSubsystem {

    private final IMotor motor;
    private final FunnelStuff funnelStuff;
    private final IDigitalInput shooterDigitalInput;
    private final IDigitalInput ampDigitalInput;
    private final DigitalInputInputsAutoLogged shooterDigitalInputInputs;
    private final DigitalInputInputsAutoLogged ampDigitalInputInputs;
    private Rotation2d targetPosition;

    public Funnel(FunnelStuff funnelStuff) {
        super(funnelStuff.logPath());
        this.motor = funnelStuff.motor();
        this.shooterDigitalInput = funnelStuff.shooterDigitalInput();
        this.ampDigitalInput = funnelStuff.ampDigitalInput();
        this.funnelStuff = funnelStuff;
        this.shooterDigitalInputInputs = new DigitalInputInputsAutoLogged();
        this.ampDigitalInputInputs = new DigitalInputInputsAutoLogged();

        this.targetPosition = new Rotation2d();
        update();
    }

    public boolean isObjectInShooter() {
        return shooterDigitalInputInputs.debouncedValue;
    }

    public boolean isObjectInAmp() {
        return ampDigitalInputInputs.debouncedValue;
    }

    public void update() {
        shooterDigitalInput.updateInputs(shooterDigitalInputInputs);
        ampDigitalInput.updateInputs(ampDigitalInputInputs);
        motor.updateSignals(funnelStuff.voltageSignal(), funnelStuff.positionSignal());
    }

    public void setPower(double power) {
        motor.setPower(power);
    }

    public void stop() {
        motor.stop();
    }

    public void setBrake(boolean brake) {
        motor.setBrake(brake);
    }

    public Rotation2d getPosition() {
        return funnelStuff.positionSignal().getLatestValue();
    }

    public void setTargetPosition(double rotations) {
        this.targetPosition = Rotation2d.fromRotations(getPosition().getRotations() + rotations);
    }

    public boolean isAtPosition(Rotation2d position){
        return (getPosition().getRotations() - position.getRotations() <= 5);
    }

    public boolean isPastPosition() {
        return getPosition().getRotations() > targetPosition.getRotations();
    }

    @Override
    protected void subsystemPeriodic() {
        update();
        Logger.processInputs(funnelStuff.shooterDigitalInputLogPath(), shooterDigitalInputInputs);
        Logger.processInputs(funnelStuff.ampDigitalInputLogPath(), ampDigitalInputInputs);
        Logger.recordOutput(funnelStuff.logPath() + "IsObjectInShooter", isObjectInShooter());
        Logger.recordOutput(funnelStuff.logPath() + "IsObjectInAmp", isObjectInAmp());
    }

}

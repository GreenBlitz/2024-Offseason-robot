package frc.robot.subsystems.chooser;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.hardware.digitalinput.DigitalInputInputsAutoLogged;
import frc.robot.hardware.digitalinput.IDigitalInput;
import frc.robot.hardware.motor.IMotor;
import frc.utils.GBSubsystem;
import org.littletonrobotics.junction.Logger;

public class Chooser extends GBSubsystem {

    private final IMotor motor;
    private final ChooserAvatiach chooserAvatiach;
    private final ChooserCommandsBuilder commandBuilder;
    private final IDigitalInput digitalInput;
    private final DigitalInputInputsAutoLogged digitalInputInputs;
    private Rotation2d targetPosition;

    public Chooser(ChooserAvatiach chooserAvatiach) {
        super(chooserAvatiach.logPath());
        this.motor = chooserAvatiach.motor();
        this.digitalInput = chooserAvatiach.digitalInput();
        this.chooserAvatiach = chooserAvatiach;
        this.digitalInputInputs = new DigitalInputInputsAutoLogged();

        this.commandBuilder = new ChooserCommandsBuilder(this);
        this.targetPosition = new Rotation2d();
    }

    public boolean isObjectIn() {
        return digitalInputInputs.debouncedValue;
    }

    public void update() {
        digitalInput.updateInputs(digitalInputInputs);
        motor.updateSignals(chooserAvatiach.voltageSignal(), chooserAvatiach.positionSignal());
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
        return chooserAvatiach.positionSignal().getLatestValue();
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
        Logger.processInputs(chooserAvatiach.digitalInputLogPath(), digitalInputInputs);
        Logger.recordOutput(chooserAvatiach.logPath() + "IsObjectIn", isObjectIn());
    }

}

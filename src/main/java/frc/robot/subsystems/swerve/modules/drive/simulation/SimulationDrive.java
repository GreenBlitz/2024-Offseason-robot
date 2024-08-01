package frc.robot.subsystems.swerve.modules.drive.simulation;

import com.ctre.phoenix6.controls.VoltageOut;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.simulation.SimpleMotorSimulation;
import frc.robot.subsystems.swerve.modules.ModuleConstants;
import frc.robot.subsystems.swerve.modules.drive.IDrive;
import frc.robot.subsystems.swerve.modules.ModuleInputsContainer;

public class SimulationDrive implements IDrive {

    private final SimpleMotorSimulation driveMotor;

    private final VoltageOut voltageRequest;

    public SimulationDrive(SimulationDriveConstants constants){
        this.driveMotor = constants.driveMotor();
        this.voltageRequest = new VoltageOut(0).withEnableFOC(constants.enableFOC());
    }

    @Override
    public void setBrake(boolean brake) {

    }


    @Override
    public void stop() {
        driveMotor.stop();
    }

    @Override
    public void runMotorByVoltage(double voltage) {
        driveMotor.setControl(voltageRequest.withOutput(voltage));
    }

    @Override
    public void setTargetClosedLoopVelocity(Rotation2d velocityPerSecond) {
        runMotorByVoltage(velocityPerSecond, , ModuleConstants.VOLTAGE_COMPENSATION_SATURATION);
    }


    @Override
    public void updateInputs(ModuleInputsContainer inputs) {
        inputs.getDriveMotorInputs().angle = driveMotor.getPosition();
        inputs.getDriveMotorInputs().velocity = driveMotor.getVelocity();
        inputs.getDriveMotorInputs().current = driveMotor.getCurrent();
        inputs.getDriveMotorInputs().voltage = driveMotor.getVoltage();
        inputs.getDriveMotorInputs().angleOdometrySamples = new Rotation2d[]{inputs.getDriveMotorInputs().angle};
    }

}

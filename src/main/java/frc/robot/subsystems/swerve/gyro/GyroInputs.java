package frc.robot.subsystems.swerve.gyro;

import edu.wpi.first.math.geometry.Rotation2d;
import org.littletonrobotics.junction.AutoLog;

@AutoLog
public class GyroInputs {

    public Rotation2d gyroYaw = new Rotation2d();

    public Rotation2d gyroPitch = new Rotation2d();

    public double accelerationX = 0;

    public double accelerationY = 0;

    public double accelerationZ = 0;

    public double[] odometryUpdatesYawDegrees = new double[0];

    public double[] odometryUpdatesTimestamp = new double[0];

}

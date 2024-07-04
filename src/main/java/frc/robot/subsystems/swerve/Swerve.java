package frc.robot.subsystems.swerve;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveWheelPositions;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.Robot;
import frc.robot.constants.MathConstants;
import frc.robot.poseestimation.observations.OdometryObservation;
import frc.robot.subsystems.swerve.gyro.SwerveGyroConstants;
import frc.robot.subsystems.swerve.gyro.gyrointerface.ISwerveGyro;
import frc.robot.subsystems.swerve.gyro.gyrointerface.SwerveGyroFactory;
import frc.robot.subsystems.swerve.gyro.gyrointerface.SwerveGyroInputsAutoLogged;
import frc.robot.subsystems.swerve.modules.Module;
import frc.robot.subsystems.swerve.modules.ModuleUtils;
import frc.robot.subsystems.swerve.swervestatehelpers.AimAssist;
import frc.robot.subsystems.swerve.swervestatehelpers.DriveRelative;
import frc.utils.DriverStationUtils;
import frc.utils.GBSubsystem;
import frc.utils.cycletime.CycleTimeUtils;
import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.Logger;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Swerve extends GBSubsystem {

    public static final Lock ODOMETRY_LOCK = new ReentrantLock();

    private final SwerveGyroInputsAutoLogged gyroInputs;
    private final ISwerveGyro gyro;
    private final Module[] modules;
    private final SwerveState currentState;

    public Swerve() {
        setName(getClass().getSimpleName());
        this.currentState = new SwerveState(SwerveState.DEFAULT_DRIVE);
        this.modules = getModules();
        this.gyro = SwerveGyroFactory.createSwerveGyro();
        this.gyroInputs = new SwerveGyroInputsAutoLogged();
    }

    private Module[] getModules() {
        return new Module[]{
                new Module(ModuleUtils.ModuleName.FRONT_LEFT),
                new Module(ModuleUtils.ModuleName.FRONT_RIGHT),
                new Module(ModuleUtils.ModuleName.BACK_LEFT),
                new Module(ModuleUtils.ModuleName.BACK_RIGHT),
        };
    }

    @Override
    protected String getLogPath() {
        return SwerveConstants.SWERVE_LOG_PATH;
    }


    @Override
    public void subsystemPeriodic() {
        logState();
        logFieldRelativeVelocities();
    }

    private void logState() {
        Logger.recordOutput(SwerveConstants.SWERVE_STATE_LOG_PATH + "DriveMode", currentState.getDriveMode());
        Logger.recordOutput(SwerveConstants.SWERVE_STATE_LOG_PATH + "DriveSpeed", currentState.getDriveSpeed());
        Logger.recordOutput(SwerveConstants.SWERVE_STATE_LOG_PATH + "LoopMode", currentState.getLoopMode());
        Logger.recordOutput(SwerveConstants.SWERVE_STATE_LOG_PATH + "RotateAxis", currentState.getRotateAxis());
        Logger.recordOutput(SwerveConstants.SWERVE_STATE_LOG_PATH + "AimAssist", currentState.getAimAssist());
    }

    private void logFieldRelativeVelocities() {
        ChassisSpeeds fieldRelativeSpeeds = getFieldRelativeVelocity();
        Logger.recordOutput(SwerveConstants.SWERVE_VELOCITY_LOG_PATH + "Rotation", fieldRelativeSpeeds.omegaRadiansPerSecond);
        Logger.recordOutput(SwerveConstants.SWERVE_VELOCITY_LOG_PATH + "X", fieldRelativeSpeeds.vxMetersPerSecond);
        Logger.recordOutput(SwerveConstants.SWERVE_VELOCITY_LOG_PATH + "Y", fieldRelativeSpeeds.vyMetersPerSecond);
        Logger.recordOutput(SwerveConstants.SWERVE_VELOCITY_LOG_PATH + "Magnitude", getDriveMagnitude(fieldRelativeSpeeds));
    }

    public void updateInputs() {
        ODOMETRY_LOCK.lock(); {
            gyro.updateInputs(gyroInputs);
            Logger.processInputs(SwerveGyroConstants.LOG_PATH, gyroInputs);

            for (Module currentModule : modules) {
                currentModule.logStatus();
            }
        } ODOMETRY_LOCK.unlock();
    }


    protected void initializeDrive(SwerveState updatedState) {
        currentState.updateState(updatedState);
        setClosedLoopForModules();
        resetRotationController();
    }

    protected void setClosedLoopForModules() {
        for (Module currentModule : modules) {
            currentModule.setDriveMotorClosedLoop(currentState.getLoopMode().isClosedLoop);
        }
    }

    protected void setBrake(boolean brake) {
        for (Module currentModule : modules) {
            currentModule.setBrake(brake);
        }
    }

    public void setHeading(Rotation2d heading) {
        gyro.setHeading(heading);
    }


    public Rotation2d getAbsoluteHeading() {
        double inputtedHeadingRads = MathUtil.angleModulus(gyroInputs.gyroYaw.getRadians());
        return Rotation2d.fromRadians(inputtedHeadingRads);
    }

    public Rotation2d getRelativeHeading() {
        return Rotation2d.fromDegrees(gyroInputs.gyroYaw.getDegrees());
    }

    public Translation3d getGyroAcceleration() {
        return new Translation3d(gyroInputs.accelerationX, gyroInputs.accelerationY, gyroInputs.accelerationZ);
    }


    protected void resetModulesAngleByEncoder() {
        for (Module module : getModules()) {
            module.resetByEncoder();
        }
    }

    protected void resetRotationController() {
        SwerveConstants.ROTATION_PID_DEGREES_CONTROLLER.reset();
    }


    @AutoLogOutput(key = SwerveConstants.SWERVE_LOG_PATH + "IsModulesAtStates")
    public boolean isModulesAtStates() {
        for (Module module : modules) {
            if (!module.isAtTargetState()) {
                return false;
            }
        }
        return true;
    }

    @AutoLogOutput(key = SwerveConstants.SWERVE_LOG_PATH + "TargetModulesStates")
    private SwerveModuleState[] getTargetStates() {
        SwerveModuleState[] states = new SwerveModuleState[modules.length];

        for (int i = 0; i < modules.length; i++) {
            states[i] = modules[i].getTargetState();
        }

        return states;
    }

    @AutoLogOutput(key = SwerveConstants.SWERVE_LOG_PATH + "CurrentModulesStates")
    private SwerveModuleState[] getModulesStates() {
        SwerveModuleState[] states = new SwerveModuleState[modules.length];

        for (int i = 0; i < modules.length; i++) {
            states[i] = modules[i].getCurrentState();
        }

        return states;
    }

    private void setTargetModuleStates(SwerveModuleState[] swerveModuleStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, SwerveConstants.MAX_SPEED_METERS_PER_SECOND);
        for (int i = 0; i < modules.length; i++) {
            modules[i].setTargetState(swerveModuleStates[i]);
        }
    }

    protected Rotation2d[] getModulesDriveDistances() {
        return Arrays.stream(modules).map(Module::getDriveDistanceAngle).toArray(Rotation2d[]::new);
    }

    private SwerveDriveWheelPositions getSwerveWheelPositions(int odometryUpdateIndex) {
        SwerveModulePosition[] swerveModulePositions = new SwerveModulePosition[modules.length];
        for (int i = 0; i < modules.length; i++) {
            swerveModulePositions[i] = modules[i].getOdometryPosition(odometryUpdateIndex);
        }
        return new SwerveDriveWheelPositions(swerveModulePositions);
    }

    public OdometryObservation[] getAllOdometryObservations() {
        double[] timestamps = gyroInputs.odometryUpdatesTimestamp;
        Rotation2d[] gyroRotations = gyroInputs.odometryUpdatesYaw;
        int odometryUpdates = timestamps.length;

        SwerveDriveWheelPositions[] swerveWheelPositions = new SwerveDriveWheelPositions[odometryUpdates];
        for (int i = 0; i < odometryUpdates; i++) {
            swerveWheelPositions[i] = getSwerveWheelPositions(i);
        }

        OdometryObservation[] odometryObservations = new OdometryObservation[odometryUpdates];
        for (int i = 0; i < odometryUpdates; i++) {
            odometryObservations[i] = new OdometryObservation(swerveWheelPositions[i], gyroRotations[i], timestamps[i]);
        }

        return odometryObservations;
    }



    public ChassisSpeeds getSelfRelativeVelocity() {
        return SwerveConstants.KINEMATICS.toChassisSpeeds(getModulesStates());
    }

    public ChassisSpeeds getFieldRelativeVelocity() {
        return ChassisSpeeds.fromRobotRelativeSpeeds(getSelfRelativeVelocity(), Robot.poseEstimator.getCurrentPose().getRotation());
    }

    private ChassisSpeeds getDriveModeRelativeChassisSpeeds(ChassisSpeeds chassisSpeeds, SwerveState swerveState) {
        if (swerveState.getDriveMode() == DriveRelative.SELF_RELATIVE) {
            return chassisSpeeds;
        }
        else {
            return fieldRelativeSpeedsToSelfRelativeSpeeds(chassisSpeeds);
        }
    }

    public static Rotation2d getAllianceRelativeAngle() {
        Rotation2d currentAngle = Robot.poseEstimator.getCurrentPose().getRotation();
        return DriverStationUtils.isRedAlliance() ? currentAngle.rotateBy(Rotation2d.fromDegrees(180)) : currentAngle;
    }

    private static double getDriveMagnitude(ChassisSpeeds chassisSpeeds){
        return Math.sqrt(Math.pow(chassisSpeeds.vxMetersPerSecond,2) + Math.pow(chassisSpeeds.vyMetersPerSecond,2));
    }


    public void pointWheels(Rotation2d targetAngle, boolean optimize) {
        for (Module module : modules) {
            module.pointToAngle(targetAngle, optimize);
        }
    }

    public void pointWheelsInX() {
        SwerveModuleState frontLeftBackRight = new SwerveModuleState(0, MathConstants.EIGHTH_CIRCLE);
        SwerveModuleState frontRightBackLeft = new SwerveModuleState(0, MathConstants.EIGHTH_CIRCLE.unaryMinus());

        modules[0].setTargetState(frontLeftBackRight);
        modules[1].setTargetState(frontRightBackLeft);
        modules[2].setTargetState(frontRightBackLeft);
        modules[3].setTargetState(frontLeftBackRight);
    }

    public void pointWheelsInCircle() {
        SwerveModuleState frontLeftBackRight = new SwerveModuleState(0, MathConstants.EIGHTH_CIRCLE.unaryMinus());
        SwerveModuleState frontRightBackLeft = new SwerveModuleState(0, MathConstants.EIGHTH_CIRCLE);

        modules[0].setTargetState(frontLeftBackRight);
        modules[1].setTargetState(frontRightBackLeft);
        modules[2].setTargetState(frontRightBackLeft);
        modules[3].setTargetState(frontLeftBackRight);
    }


    /**
     * Runs swerve around itself for WheelRadiusCharacterization
     *
     * @param omegaPerSec - velocity to run the swerve
     */
    public void runWheelRadiusCharacterization(Rotation2d omegaPerSec) {
        driveByState(new ChassisSpeeds(0, 0, omegaPerSec.getRadians()));
    }

    /**
     * Runs swerve module around itself for Sysid Steer Calibration
     *
     * @param voltage - voltage to run the swerve module steer
     */
    public void runModuleSteerByVoltage(ModuleUtils.ModuleName module, double voltage) {
        modules[module.getIndex()].runSteerMotorByVoltage(voltage);
    }

    /**
     * Runs swerve module around itself for Sysid Steer Calibration
     *
     * @param voltage - voltage to run the swerve module drive
     */
    public void runModulesDriveByVoltage(double voltage) {
        for (Module module : modules) {
            module.runDriveMotorByVoltage(voltage);
        }
    }


    protected void pidToPose(Pose2d targetBluePose) {
        Pose2d currentBluePose = Robot.poseEstimator.getCurrentPose();

        double xSpeed = SwerveConstants.TRANSLATION_PID_CONTROLLER.calculate(currentBluePose.getX(), targetBluePose.getX());
        double ySpeed = SwerveConstants.TRANSLATION_PID_CONTROLLER.calculate(currentBluePose.getY(), targetBluePose.getY());
        int direction = DriverStationUtils.isBlueAlliance() ? 1 : -1;
        Rotation2d thetaSpeed = calculateProfiledAngleSpeedToTargetAngle(targetBluePose.getRotation());

        ChassisSpeeds targetFieldRelativeSpeeds = new ChassisSpeeds(
                xSpeed * direction,
                ySpeed * direction,
                thetaSpeed.getRadians()
        );
        driveByState(targetFieldRelativeSpeeds);
    }

    protected void rotateToAngle(Rotation2d targetAngle) {
        ChassisSpeeds targetFieldRelativeSpeeds = new ChassisSpeeds(
                0,
                0,
                calculateProfiledAngleSpeedToTargetAngle(targetAngle).getRadians()
        );
        driveByState(targetFieldRelativeSpeeds);
    }


    protected void drive(double xPower, double yPower, double thetaPower) {
        driveByState(powersToSpeeds(xPower, yPower, thetaPower));
    }

    private void driveByState(ChassisSpeeds chassisSpeeds) {
        driveByState(chassisSpeeds, currentState);
    }

    public void driveByState(ChassisSpeeds chassisSpeeds, SwerveState swerveState) {
        chassisSpeeds = getDriveModeRelativeChassisSpeeds(chassisSpeeds, swerveState);
        chassisSpeeds = applyAimAssistedRotationVelocity(chassisSpeeds, swerveState);
        chassisSpeeds = discretize(chassisSpeeds);

        if (isStill(chassisSpeeds)) {
            stop();
            return;
        }

        SwerveModuleState[] swerveModuleStates = SwerveConstants.KINEMATICS.toSwerveModuleStates(
                chassisSpeeds,
                swerveState.getRotateAxis().getRotateAxis()
        );
        setTargetModuleStates(swerveModuleStates);
    }

    protected void stop() {
        for (Module currentModule : modules) {
            currentModule.stop();
        }
    }


    private ChassisSpeeds applyAimAssistedRotationVelocity(ChassisSpeeds currentSpeeds, SwerveState swerveState) {
        if (swerveState.getAimAssist().equals(AimAssist.NONE)) {
            return currentSpeeds;
        }
        //PID
        Rotation2d pidVelocity = calculateProfiledAngleSpeedToTargetAngle(swerveState.getAimAssist().targetAngleSupplier.get());

        //Magnitude Factor
        double driveMagnitude = getDriveMagnitude(currentSpeeds);
        double angularVelocityRads =
                pidVelocity.getRadians() * SwerveConstants.AIM_ASSIST_MAGNITUDE_FACTOR / (driveMagnitude + SwerveConstants.AIM_ASSIST_MAGNITUDE_FACTOR);

        //Joystick Output
        double angularVelocityWithJoystick = angularVelocityRads + currentSpeeds.omegaRadiansPerSecond;

        //Clamp
        double clampedAngularVelocity = MathUtil.clamp(
                angularVelocityWithJoystick,
                -SwerveConstants.MAX_ROTATIONAL_SPEED_PER_SECOND.getRadians(),
                SwerveConstants.MAX_ROTATIONAL_SPEED_PER_SECOND.getRadians()
        );

        //todo maybe - make value have stick range (P = MAX_ROT / MAX_ERROR = 10 rads / Math.PI) or clamp between MAX_ROT
        //todo - distance factor
        return new ChassisSpeeds(currentSpeeds.vxMetersPerSecond, currentSpeeds.vyMetersPerSecond, clampedAngularVelocity);
    }

    private ChassisSpeeds fieldRelativeSpeedsToSelfRelativeSpeeds(ChassisSpeeds fieldRelativeSpeeds) {
        return ChassisSpeeds.fromFieldRelativeSpeeds(fieldRelativeSpeeds, getAllianceRelativeAngle());
    }

    private ChassisSpeeds powersToSpeeds(double xPower, double yPower, double thetaPower) {
        return new ChassisSpeeds(
                xPower * currentState.getDriveSpeed().maxTranslationSpeedMetersPerSecond,
                yPower * currentState.getDriveSpeed().maxTranslationSpeedMetersPerSecond,
                thetaPower * currentState.getDriveSpeed().maxRotationSpeedPerSecond.getRadians()
        );
    }

    /**
     * When the robot drives while rotating it skews a bit to the side.
     * This should fix the chassis speeds, so they won't make the robot skew while rotating.
     *
     * @param chassisSpeeds the chassis speeds to fix skewing for
     * @return the fixed speeds
     */
    private static ChassisSpeeds discretize(ChassisSpeeds chassisSpeeds) {
        return ChassisSpeeds.discretize(chassisSpeeds, CycleTimeUtils.getCurrentCycleTime());
    }

    private Rotation2d calculateProfiledAngleSpeedToTargetAngle(Rotation2d targetAngle) {
        Rotation2d currentAngle = Robot.poseEstimator.getCurrentPose().getRotation();
        return Rotation2d.fromDegrees(SwerveConstants.ROTATION_PID_DEGREES_CONTROLLER.calculate(
                currentAngle.getDegrees(),
                targetAngle.getDegrees()
        ));
    }

    private static boolean isStill(ChassisSpeeds chassisSpeeds) {
        return Math.abs(chassisSpeeds.vxMetersPerSecond) <= SwerveConstants.DRIVE_NEUTRAL_DEADBAND
                && Math.abs(chassisSpeeds.vyMetersPerSecond) <= SwerveConstants.DRIVE_NEUTRAL_DEADBAND
                && Math.abs(chassisSpeeds.omegaRadiansPerSecond) <= SwerveConstants.ROTATION_NEUTRAL_DEADBAND.getRadians();
    }

}

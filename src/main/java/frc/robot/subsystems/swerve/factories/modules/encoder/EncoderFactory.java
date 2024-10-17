package frc.robot.subsystems.swerve.factories.modules.encoder;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Robot;
import frc.robot.constants.IDs;
import frc.robot.hardware.angleencoder.EmptyAngleEncoder;
import frc.robot.hardware.signal.InputSignal;
import frc.robot.subsystems.swerve.SwerveType;
import frc.robot.subsystems.swerve.module.ModuleConstants;
import frc.robot.subsystems.swerve.module.ModuleUtils;
import frc.robot.subsystems.swerve.module.stuffs.EncoderStuff;
import org.littletonrobotics.junction.LogTable;

public class EncoderFactory {

	private static EncoderStuff createSwerveEncoder(String logPath, ModuleUtils.ModulePosition modulePosition) {
		return switch (Robot.ROBOT_TYPE) {
//			case REAL -> switch (modulePosition) {
//				case FRONT_LEFT -> EncoderRealConstants.generateEncoderStuff(logPath, IDs.CANCodersIDs.FRONT_LEFT_ENCODER);
//				case FRONT_RIGHT -> EncoderRealConstants.generateEncoderStuff(logPath, IDs.CANCodersIDs.FRONT_RIGHT_ENCODER);
//				case BACK_LEFT -> EncoderRealConstants.generateEncoderStuff(logPath, IDs.CANCodersIDs.BACK_LEFT_ENCODER);
//				case BACK_RIGHT -> EncoderRealConstants.generateEncoderStuff(logPath, IDs.CANCodersIDs.BACK_RIGHT_ENCODER);
//			};
			case SIMULATION -> null;// TODO
			case REAL -> new EncoderStuff(new EmptyAngleEncoder(logPath), new InputSignal<Rotation2d>() {
				@Override
				public Rotation2d getLatestValue() {
					return new Rotation2d();
				}

				@Override
				public Rotation2d[] asArray() {
					return new Rotation2d[0];
				}

				@Override
				public double getTimestamp() {
					return 0;
				}

				@Override
				public double[] getTimestamps() {
					return new double[0];
				}

				@Override
				public void toLog(LogTable table) {

				}

				@Override
				public void fromLog(LogTable table) {

				}
			});
		};
	}

	public static EncoderStuff create(SwerveType swerveType, ModuleUtils.ModulePosition modulePosition) {
		String logPath = swerveType.getLogPath() + ModuleConstants.LOG_PATH_ADDITION + modulePosition + "/Encoder/";
		return switch (swerveType) {
			case SWERVE -> createSwerveEncoder(logPath, modulePosition);
		};
	}

}

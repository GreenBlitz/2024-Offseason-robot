package frc.robot.subsystems.elevator;

public enum ElevatorPresets {
    SCORE(0), AMP(0), DEFAULT(0);

    private final double position;

    ElevatorPresets(double position) {
        this.position = position;
    }

    public double getPosition() {
        return position;
    }

}

package robot;

import robot.Constants.DIRECTION;

/**
 * Represents a sensor mounted on the robot.
 *
 * @author Suyash Lakhotia
 */

// @TODO: Sensor logic.

public class Sensor {
    private int range;
    private int sensorPosRow;
    private int sensorPosCol;
    private DIRECTION sensorDir;

    public Sensor(int range, int row, int col, DIRECTION dir) {
        this.range = range;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }
}

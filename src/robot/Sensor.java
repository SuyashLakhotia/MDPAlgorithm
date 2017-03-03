package robot;

import map.Map;
import robot.RobotConstants.DIRECTION;

/**
 * Represents a sensor mounted on the robot.
 *
 * @author Suyash Lakhotia
 */

public class Sensor {
    private int lowerRange;
    private int upperRange;
    private int sensorPosRow;
    private int sensorPosCol;
    private DIRECTION sensorDir;

    public Sensor(int lowerRange, int upperRange, int row, int col, DIRECTION dir) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }

    /**
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    public int getSensorVal(Map exploredMap, Map realMap, int rowInc, int colInc) {
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!realMap.checkValidCoordinates(row, col)) {
                return i;
            }

            exploredMap.getCell(row, col).setIsExplored(true);
            if (realMap.getCell(row, col).getIsObstacle()) {
                exploredMap.setObstacleCell(row, col, true);
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the number of cells to the nearest detected obstacle or -1 if no obstacle is detected.
     */
    public int sense(Map exploredMap, Map realMap) {
        switch (sensorDir) {
            case NORTH:
                return getSensorVal(exploredMap, realMap, 1, 0);
            case EAST:
                return getSensorVal(exploredMap, realMap, 0, 1);
            case SOUTH:
                return getSensorVal(exploredMap, realMap, -1, 0);
            case WEST:
                return getSensorVal(exploredMap, realMap, 0, -1);
        }
        return -1;
    }

    /**
     * Uses the sensor direction and given value from the actual sensor to update the map.
     */
    public void senseReal(Map exploredMap, int sensorVal) {
        switch (sensorDir) {
            case NORTH:
                processSensorVal(exploredMap, 1, 0, sensorVal);
                break;
            case EAST:
                processSensorVal(exploredMap, 0, 1, sensorVal);
                break;
            case SOUTH:
                processSensorVal(exploredMap, -1, 0, sensorVal);
                break;
            case WEST:
                processSensorVal(exploredMap, 0, -1, sensorVal);
                break;
        }
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    public void processSensorVal(Map exploredMap, int rowInc, int colInc, int sensorVal) {
        int upperLimit;

        if (sensorVal != 0) {
            int row = this.sensorPosRow + (rowInc * sensorVal);
            int col = this.sensorPosCol + (colInc * sensorVal);

            if (exploredMap.checkValidCoordinates(row, col)) {
                exploredMap.setObstacleCell(row, col, true);
            }

            upperLimit = sensorVal;
        } else {
            upperLimit = upperRange;
        }

        for (int i = this.lowerRange; i <= upperLimit; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col))
                continue;

            exploredMap.getCell(row, col).setIsExplored(true);
        }
    }
}

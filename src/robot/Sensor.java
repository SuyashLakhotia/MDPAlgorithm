package robot;

import map.Map;
import robot.RobotConstants.DIRECTION;

/**
 * Represents a sensor mounted on the robot.
 *
 * @author Suyash Lakhotia
 */

public class Sensor {
    private final int lowerRange;
    private final int upperRange;
    private int sensorPosRow;
    private int sensorPosCol;
    private DIRECTION sensorDir;
    private String id;

    public Sensor(int lowerRange, int upperRange, int row, int col, DIRECTION dir, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
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
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    private int getSensorVal(Map exploredMap, Map realMap, int rowInc, int colInc) {
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col)) return i;

            exploredMap.getCell(row, col).setIsExplored(true);

            if (realMap.getCell(row, col).getIsObstacle()) {
                exploredMap.setObstacleCell(row, col, true);
                return i;
            }
        }
        return -1;
    }

    /**
     * Uses the sensor direction and given value from the actual sensor to update the map.
     */
    public void senseReal(Map exploredMap, int sensorVal) {
        switch (sensorDir) {
            case NORTH:
                processSensorVal(exploredMap, sensorVal, 1, 0);
                break;
            case EAST:
                processSensorVal(exploredMap, sensorVal, 0, 1);
                break;
            case SOUTH:
                processSensorVal(exploredMap, sensorVal, -1, 0);
                break;
            case WEST:
                processSensorVal(exploredMap, sensorVal, 0, -1);
                break;
        }
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    private void processSensorVal(Map exploredMap, int sensorVal, int rowInc, int colInc) {
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col)) continue;

            exploredMap.getCell(row, col).setIsExplored(true);

            if (sensorVal == i) {
                exploredMap.setObstacleCell(row, col, true);
                break;
            }

            if (exploredMap.getCell(row, col).getIsObstacle()) {
                if (id.equals("SRFL") || id.equals("SRFC") || id.equals("SRFR")) {
                    exploredMap.setObstacleCell(row, col, false);
                } else {
                    break;
                }
            }
        }
    }
}

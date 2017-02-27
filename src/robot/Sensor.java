package robot;

import map.Map;
import robot.Constants.DIRECTION;

/**
 * Represents a sensor mounted on the robot.
 *
 * @author Suyash Lakhotia
 */

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

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }

    // Returns the number of cells to the nearest obstacle / wall.
    public int sense(Map exploredMap, Map realMap) {
        switch (sensorDir) {
            case NORTH:
                for (int i = 1; i <= this.range; i++) {
                    if (this.sensorPosRow + i >= 20) { // touching north wall
                        return i;
                    }

                    exploredMap.getCell(this.sensorPosRow + i, this.sensorPosCol).setIsExplored(true);
                    if (realMap.getCell(this.sensorPosRow + i, this.sensorPosCol).getIsObstacle()) {
                        exploredMap.setObstacleCell(this.sensorPosRow + i, this.sensorPosCol, true);
                        return i;
                    }
                }
                return 0;
            case EAST:
                for (int i = 1; i <= this.range; i++) {
                    if (this.sensorPosCol + i >= 15) { // touching east wall
                        return i;
                    }

                    exploredMap.getCell(this.sensorPosRow, this.sensorPosCol + i).setIsExplored(true);
                    if (realMap.getCell(this.sensorPosRow, this.sensorPosCol + i).getIsObstacle()) {
                        exploredMap.setObstacleCell(this.sensorPosRow, this.sensorPosCol + i, true);
                        return i;
                    }
                }
                return 0;
            case SOUTH:
                for (int i = 1; i <= this.range; i++) {
                    if (this.sensorPosRow - i <= -1) { // touching south wall
                        return i;
                    }

                    exploredMap.getCell(this.sensorPosRow - i, this.sensorPosCol).setIsExplored(true);
                    if (realMap.getCell(this.sensorPosRow - i, this.sensorPosCol).getIsObstacle()) {
                        exploredMap.setObstacleCell(this.sensorPosRow - i, this.sensorPosCol, true);
                        return i;
                    }
                }
                return 0;
            case WEST:
                for (int i = 1; i <= this.range; i++) {
                    if (this.sensorPosCol - i <= -1) { // touching west wall
                        return i;
                    }

                    exploredMap.getCell(this.sensorPosRow, this.sensorPosCol - i).setIsExplored(true);
                    if (realMap.getCell(this.sensorPosRow, this.sensorPosCol - i).getIsObstacle()) {
                        exploredMap.setObstacleCell(this.sensorPosRow, this.sensorPosCol - i, true);
                        return i;
                    }
                }
                return 0;
        }
        return 0;
    }
}

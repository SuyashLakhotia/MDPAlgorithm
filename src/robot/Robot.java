package robot;

import robot.Constants.DIRECTION;
import robot.Constants.MOVEMENT;

import java.util.concurrent.TimeUnit;

// @formatter:off
/**
 * Represents the robot moving in the arena.
 *
 * The robot is represented by a 2 x 2 cell space as below:
 *
 *          ^  ^  ^
 *         SR LR SR
 *        [X]   [X]
 *   < SR           SR >
 *        [X]   [X]
 *
 * SR = Short Range Sensor, LR = Long Range Sensor
 *
 * @author Suyash Lakhotia
 */
// @formatter:on

// @TODO: Robot speed?

public class Robot {
    private int posRow;
    private int posCol;
    private DIRECTION robotDir;
    public Sensor LRFront;
    public Sensor SRFrontLeft;
    public Sensor SRFrontRight;
    public Sensor SRLeft;
    public Sensor SRRight;
    private int speed = 1000; // time taken (ms) for one movement

    public Robot(int row, int col) {
        posRow = row;
        posCol = col;
        robotDir = Constants.START_DIR;

        LRFront = new Sensor(Constants.SENSOR_SHORT_RANGE, this.posRow + 1, this.posCol, this.robotDir);
        SRFrontLeft = new Sensor(Constants.SENSOR_SHORT_RANGE, this.posRow + 1, this.posCol - 1, this.robotDir);
        SRFrontRight = new Sensor(Constants.SENSOR_SHORT_RANGE, this.posRow + 1, this.posCol + 1, this.robotDir);
        SRLeft = new Sensor(Constants.SENSOR_SHORT_RANGE, this.posRow, this.posCol - 1, findNewDirection(MOVEMENT.LEFT));
        SRRight = new Sensor(Constants.SENSOR_LONG_RANGE, this.posRow, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
    }

    public void setRobotPos(int row, int col) {
        posRow = row;
        posCol = col;
    }

    public int getRobotPosRow() {
        return posRow;
    }

    public int getRobotPosCol() {
        return posCol;
    }

    public void setRobotDir(DIRECTION dir) {
        robotDir = dir;
    }

    public DIRECTION getRobotCurDir() {
        return robotDir;
    }

    public void move(MOVEMENT m) {
        // Emulate real movement by pausing execution.
        try {
            TimeUnit.MILLISECONDS.sleep(speed);
        } catch (InterruptedException e) {
            System.out.println("Something went wrong in Robot.move()!");
        }

        switch (m) {
            case FORWARD:
                switch (robotDir) {
                    case NORTH:
                        posRow++;
                        break;
                    case EAST:
                        posCol++;
                        break;
                    case SOUTH:
                        posRow--;
                        break;
                    case WEST:
                        posCol--;
                        break;
                }
                break;
            case BACKWARD:
                switch (robotDir) {
                    case NORTH:
                        posRow--;
                        break;
                    case EAST:
                        posCol--;
                        break;
                    case SOUTH:
                        posRow++;
                        break;
                    case WEST:
                        posCol++;
                        break;
                }
                break;
            case RIGHT:
            case LEFT:
                robotDir = findNewDirection(m);
                break;
            case UTURN:
                robotDir = findNewDirection(MOVEMENT.RIGHT);
                robotDir = findNewDirection(MOVEMENT.RIGHT);
                break;
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }
    }

    public void setSensors() {
        switch (robotDir) {
            case NORTH:
                LRFront.setSensor(this.posRow + 1, this.posCol, this.robotDir);
                SRFrontLeft.setSensor(this.posRow + 1, this.posCol - 1, this.robotDir);
                SRFrontRight.setSensor(this.posRow + 1, this.posCol + 1, this.robotDir);
                SRLeft.setSensor(this.posRow, this.posCol - 1, findNewDirection(MOVEMENT.LEFT));
                SRRight.setSensor(this.posRow, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case EAST:
                LRFront.setSensor(this.posRow, this.posCol + 1, this.robotDir);
                SRFrontLeft.setSensor(this.posRow + 1, this.posCol + 1, this.robotDir);
                SRFrontRight.setSensor(this.posRow - 1, this.posCol + 1, this.robotDir);
                SRLeft.setSensor(this.posRow + 1, this.posCol, findNewDirection(MOVEMENT.LEFT));
                SRRight.setSensor(this.posRow - 1, this.posCol, findNewDirection(MOVEMENT.RIGHT));
                break;
            case SOUTH:
                LRFront.setSensor(this.posRow - 1, this.posCol, this.robotDir);
                SRFrontLeft.setSensor(this.posRow - 1, this.posCol + 1, this.robotDir);
                SRFrontRight.setSensor(this.posRow - 1, this.posCol - 1, this.robotDir);
                SRLeft.setSensor(this.posRow, this.posCol + 1, findNewDirection(MOVEMENT.LEFT));
                SRRight.setSensor(this.posRow, this.posCol - 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case WEST:
                LRFront.setSensor(this.posRow, this.posCol - 1, this.robotDir);
                SRFrontLeft.setSensor(this.posRow - 1, this.posCol - 1, this.robotDir);
                SRFrontRight.setSensor(this.posRow + 1, this.posCol - 1, this.robotDir);
                SRLeft.setSensor(this.posRow - 1, this.posCol, findNewDirection(MOVEMENT.LEFT));
                SRRight.setSensor(this.posRow + 1, this.posCol, findNewDirection(MOVEMENT.RIGHT));
                break;
        }

    }

    public DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT) {
            return DIRECTION.getNext(robotDir);
        } else {
            return DIRECTION.getPrevious(robotDir);
        }
    }
}

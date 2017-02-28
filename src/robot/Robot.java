package robot;

import map.Map;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;

import java.util.concurrent.TimeUnit;

// @formatter:off
/**
 * Represents the robot moving in the arena.
 *
 * The robot is represented by a 3 x 3 cell space as below:
 *
 *          ^   ^   ^
 *         SR  LR  SR
 *        [X] [X] [X]
 *   < SR [X] [X] [X] SR >
 *        [X] [X] [X]
 *
 * SR = Short Range Sensor, LR = Long Range Sensor
 *
 * @author Suyash Lakhotia
 */
// @formatter:on

public class Robot {
    private int posRow; // center cell
    private int posCol; // center cell
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
        robotDir = RobotConstants.START_DIR;

        LRFront = new Sensor(RobotConstants.SENSOR_LONG_RANGE, this.posRow + 1, this.posCol, this.robotDir);
        SRFrontLeft = new Sensor(RobotConstants.SENSOR_SHORT_RANGE, this.posRow + 1, this.posCol - 1, this.robotDir);
        SRFrontRight = new Sensor(RobotConstants.SENSOR_SHORT_RANGE, this.posRow + 1, this.posCol + 1, this.robotDir);
        SRLeft = new Sensor(RobotConstants.SENSOR_SHORT_RANGE, this.posRow, this.posCol - 1, findNewDirection(MOVEMENT.LEFT));
        SRRight = new Sensor(RobotConstants.SENSOR_SHORT_RANGE, this.posRow, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
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

    /**
     * Takes in a MOVEMENT and moves the robot accordingly by changing its position and direction.
     */
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

    /**
     * Sets the sensors' position and direction values according to the robot's current position and direction.
     */
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

    /**
     * Uses the current direction of the robot and the given movement to find the new direction of the robot.
     */
    public DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT) {
            return DIRECTION.getNext(robotDir);
        } else {
            return DIRECTION.getPrevious(robotDir);
        }
    }

    /**
     * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
     *
     * @return [LRFront, SRFrontLeft, SRFrontRight, SRLeft, SRRight]
     */
    public int[] sense(Map explorationMap, Map realMap) {
        int[] result = new int[5];
        result[0] = LRFront.sense(explorationMap, realMap);
        result[1] = SRFrontLeft.sense(explorationMap, realMap);
        result[2] = SRFrontRight.sense(explorationMap, realMap);
        result[3] = SRLeft.sense(explorationMap, realMap);
        result[4] = SRRight.sense(explorationMap, realMap);
        return result;
    }
}

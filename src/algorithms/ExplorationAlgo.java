package algorithms;

import map.Cell;
import map.Map;
import map.MapConstants;
import robot.Robot;
import robot.RobotConstants;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;

/**
 * Exploration algorithm for the robot.
 *
 * @author Priyanshu Singh
 * @author Suyash Lakhotia
 */

public class ExplorationAlgo {
    private Map exMap, realMap;
    private Robot bot;
    private int areaExplored;
    private int coverageLimit;
    private int timeLimit;
    private long startTime;
    private long endTime;

    public ExplorationAlgo(Map exMap, Map realMap, Robot bot, int coverageLimit, int timeLimit) {
        this.exMap = exMap;
        this.realMap = realMap;
        this.bot = bot;
        this.coverageLimit = coverageLimit;
        this.timeLimit = timeLimit;
    }

    /**
     * Method to run the exploration algorithm
     */
    public void runExploration() {
        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);

        bot.setSensors();
        bot.sense(exMap, realMap);
        areaExplored = calculateAreaExplored();
        System.out.println("Explored Area: " + areaExplored);
        exMap.repaint();

        // Start exploration from START and come back to START
        looping(RobotConstants.START_ROW, RobotConstants.START_COL);

        // Keep exploring till all cells are explored
        while (areaExplored != 300 && areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime) {
            System.out.println("Exploration is still not complete...");

            // Get the closest explored (to unexplored) cell and closest unexplored cell
            Cell closestUnexplored[] = closestRowUnexploredCells();
            System.out.println("Closest Unexplored Cell is: " + closestUnexplored[1].getRow() + ", " + closestUnexplored[1].getCol());

            goToNearestExploredCell(closestUnexplored);

            looping(closestUnexplored[0].getRow(), closestUnexplored[0].getCol());
        }

        System.out.println("Exploration of all cells complete");

        // Return to START after exploration and point the bot NORTH
        FastestPathAlgo returnToStart = new FastestPathAlgo(exMap, bot);
        returnToStart.runFastestPath(RobotConstants.START_ROW, RobotConstants.START_COL);
        turnBotDirection(DIRECTION.NORTH);
    }

    /**
     * Loops through robot movements until robot reaches (r, c).
     */
    private void looping(int r, int c) {
        MOVEMENT curMove = null;
        MOVEMENT previousMove;
        do {
            previousMove = curMove;
            curMove = getNextMove(previousMove);
            System.out.println("Move: " + curMove.print(curMove));
            bot.move(curMove);
            bot.setSensors();
            bot.sense(exMap, realMap);
            areaExplored = calculateAreaExplored();
            System.out.println("Area explored: " + areaExplored);
            exMap.repaint();
            if (areaExplored == 300) {
                return;
            }
        }
        while ((bot.getRobotPosRow() != r || bot.getRobotPosCol() != c) && areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime);
    }

    /**
     * Returns the next move according to the left-sticking rule.
     */
    private MOVEMENT getNextMove(MOVEMENT previousMove) {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();

        System.out.println("Current robot position: (" + botRow + ", " + botCol + ")");
        System.out.println("Current robot direction: " + bot.getRobotCurDir());

        switch (bot.getRobotCurDir()) {
            case NORTH:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("n00");
                    return MOVEMENT.FORWARD;
                }

                if (northFree() && !westFree()) {
                    System.out.println("n01");
                    return MOVEMENT.FORWARD;
                }

                if (westFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("n02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("n03");
                    return MOVEMENT.FORWARD;
                }

                if (eastFree() && !northFree()) {
                    System.out.println("n04");
                    return MOVEMENT.RIGHT;
                } else {
                    System.out.println("north error");
                    if (northFree()) {
                        System.out.println("n05");
                        return MOVEMENT.FORWARD;
                    }
                    System.out.println("n06");
                    return MOVEMENT.RIGHT;
                }

            case EAST:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("e00");
                    return MOVEMENT.FORWARD;
                }

                if (eastFree() && !northFree()) {
                    System.out.println("e01");
                    return MOVEMENT.FORWARD;

                }

                if (northFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("e02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("e03");
                    return MOVEMENT.FORWARD;
                }

                if (southFree() && !eastFree()) {
                    System.out.println("e04");
                    return MOVEMENT.RIGHT;
                } else {
                    System.out.println("east error");
                    if (eastFree()) {
                        System.out.println("e05");
                        return MOVEMENT.FORWARD;
                    }
                    System.out.println("e06");
                    return MOVEMENT.RIGHT;
                }

            case SOUTH:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("s00");
                    return MOVEMENT.FORWARD;
                }

                if (southFree() && !eastFree()) {
                    System.out.println("s01");
                    return MOVEMENT.FORWARD;
                }

                if (eastFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("s02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("s03");
                    return MOVEMENT.FORWARD;
                }

                if (westFree() && !southFree()) {
                    System.out.println("s04");
                    return MOVEMENT.RIGHT;
                } else {
                    System.out.println("south error");
                    if (southFree()) {
                        System.out.println("s05");
                        return MOVEMENT.FORWARD;
                    }
                    System.out.println("s06");
                    return MOVEMENT.RIGHT;
                }

            case WEST:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("w00");
                    return MOVEMENT.FORWARD;
                }

                if (westFree() && !southFree()) {
                    System.out.println("w01");
                    return MOVEMENT.FORWARD;
                }

                if (southFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("w02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("w03");
                    return MOVEMENT.FORWARD;
                }

                if (northFree() && !westFree()) {
                    System.out.println("w04");
                    return MOVEMENT.RIGHT;
                } else {
                    System.out.println("west error");
                    if (westFree()) {
                        System.out.println("w05");
                        return MOVEMENT.FORWARD;
                    }
                    System.out.println("w06");
                    return MOVEMENT.RIGHT;
                }

            default:
                return MOVEMENT.ERROR;
        }
    }

    /**
     * Returns an array of two cells [Nearest Explored to ret[1], Nearest Unexplored].
     */
    private Cell[] closestRowUnexploredCells() {
        Cell arr[] = new Cell[2];
        for (int r = 0; r < MapConstants.MAP_ROWS; r++) {
            for (int c = 0; c < MapConstants.MAP_COLS; c++) {
                Cell tmp = exMap.getCell(r, c);
                if (!tmp.getIsExplored()) {
                    Cell nearestExploredCell = checkForNearestExploredCell(tmp);
                    if (nearestExploredCell != null) {
                        System.out.println("Closest Unexplored Cell is (" + r + ", " + c + ")");
                        arr[0] = nearestExploredCell;
                        arr[1] = tmp;
                        return arr;
                    } else {
                        System.out.println("No near explored cells for (" + r + ", " + c + ")");
                    }
                }
            }
        }
        return null;
    }

    private Cell checkForNearestExploredCell(Cell c) {
        int c_row = c.getRow();
        int c_col = c.getCol();
        Cell cellNearOb = null;

        System.out.println("Checking for nearest explored cell for (" + c_row + ", " + c_col + ")");
        if (isExploredAndFree(c_row - 2, c_col)) {
            //south of the obstacle facing east
            cellNearOb = exMap.getCell(c_row - 2, c_col);
        } else if (isExploredAndFree(c_row + 2, c_col)) {
            //north of the obstacle facing west
            cellNearOb = exMap.getCell(c_row + 2, c_col);
        } else if (isExploredAndFree(c_row, c_col - 2)) {
            //west of the obstacle facing south
            cellNearOb = exMap.getCell(c_row, c_col - 2);
        } else if (isExploredAndFree(c_row, c_col + 2)) {
            //east of the obstacle facing north
            cellNearOb = exMap.getCell(c_row, c_col + 2);
        }

        return cellNearOb;
    }

    /**
     * Moves the robot to the nearest explored cell, turns it with the unexplored cell to the left and calls the
     * looping() method.
     */
    private void goToNearestExploredCell(Cell cells[]) {
        int exploredRow = cells[0].getRow();
        int exploredCol = cells[0].getCol();
        int unexploredRow = cells[1].getRow();
        int unexploredCol = cells[1].getCol();

        DIRECTION direction = null;

        if (exploredRow == unexploredRow - 2) {
            direction = DIRECTION.EAST;
        } else if (exploredRow == unexploredRow + 2) {
            direction = DIRECTION.WEST;
        } else if (exploredCol == unexploredCol - 2) {
            direction = DIRECTION.SOUTH;
        } else if (exploredCol == unexploredCol + 2) {
            direction = DIRECTION.NORTH;
        }

        if (cells[0] == null && direction == null) {
            System.out.println("both are null");
        } else {
            System.out.println("Going to (" + exploredRow + ", " + exploredCol + ") with direction " + direction);
        }

        // Go to the nearest explored cell
        FastestPathAlgo fpa = new FastestPathAlgo(exMap, bot, realMap);
        fpa.runFastestPath(exploredRow, exploredCol);

        areaExplored = calculateAreaExplored();
        turnBotDirection(direction);
    }

    /**
     * Turns the robot to the required direction.
     */
    private void turnBotDirection(DIRECTION targetDir) {
        DIRECTION curDir = bot.getRobotCurDir();
        int numOfTurn = Math.abs(curDir.ordinal() - targetDir.ordinal());

        System.out.println("Robot direction: " + bot.getRobotCurDir());

        if (numOfTurn == 1) {
            if (curDir.getNext(curDir) == targetDir) {
                bot.move(MOVEMENT.RIGHT);
            } else {
                bot.move(MOVEMENT.LEFT);
            }

            bot.setSensors();
            bot.sense(exMap, realMap);
            exMap.repaint();
        } else if (numOfTurn == 2) {
            bot.move(MOVEMENT.RIGHT);
            bot.setSensors();
            bot.sense(exMap, realMap);
            exMap.repaint();
            bot.move(MOVEMENT.RIGHT);
            bot.setSensors();
            bot.sense(exMap, realMap);
            exMap.repaint();
        }

        System.out.println("Robot direction: " + bot.getRobotCurDir());
    }

    /**
     * Returns true if the robot can move to the absolute north position.
     */
    private boolean northFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow + 1, botCol - 1) && isExploredAndFree(botRow + 1, botCol) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the absolute east position.
     */
    private boolean eastFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol + 1) && isExploredAndFree(botRow, botCol + 1) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the absolute south position.
     */
    private boolean southFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow - 1, botCol) && isExploredNotObstacle(botRow - 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the absolute west position.
     */
    private boolean westFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow, botCol - 1) && isExploredNotObstacle(botRow + 1, botCol - 1));
    }

    /**
     * Returns true for cells that are explored and not obstacles.
     */
    private boolean isExploredNotObstacle(int r, int c) {
        boolean b = false;
        if (exMap.checkValidCoordinates(r, c)) {
            Cell tmp = exMap.getCell(r, c);
            b = (tmp.getIsExplored() && (!tmp.getIsObstacle()));
        }

        return b;
    }

    /**
     * Returns true for cells that are explored, not virtual walls and not obstacles.
     */
    private boolean isExploredAndFree(int r, int c) {
        if (exMap.checkValidCoordinates(r, c)) {
            Cell b = exMap.getCell(r, c);
            return (b.getIsExplored() && !b.getIsVirtualWall() && !b.getIsObstacle());
        }
        return false;
    }

    /**
     * Returns the number of cells explored in the grid.
     */
    private int calculateAreaExplored() {
        int result = 0;
        for (int r = 0; r < MapConstants.MAP_ROWS; r++) {
            for (int c = 0; c < MapConstants.MAP_COLS; c++) {
                if (exMap.getCell(r, c).getIsExplored()) {
                    result++;
                }
            }
        }
        return result;
    }
}

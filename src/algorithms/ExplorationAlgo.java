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
    private int numOfContinuousExplored;
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
     * Main method that is called to start the exploration.
     */
    public void runExploration() {
        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);

        senseAndRepaint();
        areaExplored = calculateAreaExplored();
        System.out.println("Explored Area: " + areaExplored);

        runExploration(RobotConstants.START_ROW, RobotConstants.START_COL);
    }

    /**
     * Overloaded method to start or restart the exploration from a specified cell.
     */
    private void runExploration(int startRow, int startCol) {
        // Start exploration from starting cell
        explorationLoop(startRow, startCol);

        // If termination conditions are not met
        if (areaExplored != 300 && areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime) {
            System.out.println("Exploration is still not complete...");

            // Get the closest unexplored cell
            closestUnexploredCell(0, 0);
        } else {
            System.out.println("Exploration complete!");
            System.out.println(coverageLimit / 300.0 + "% Coverage");
            System.out.println((System.currentTimeMillis() - startTime) / 1000 + " seconds");

            // Return to START after exploration and point the bot northwards
            FastestPathAlgo returnToStart = new FastestPathAlgo(exMap, bot);
            returnToStart.runFastestPath(RobotConstants.START_ROW, RobotConstants.START_COL);
            turnBotDirection(DIRECTION.NORTH);
        }
    }

    /**
     * Loops through robot movements until one (or more) of the following conditions is met:
     * 1. Robot is back at (r, c)
     * 2. areaExplored > coverageLimit
     * 3. timeTaken > timeLimit
     * 4. numOfContinuousExplored > 10
     */
    private void explorationLoop(int r, int c) {
        MOVEMENT curMove = null;
        MOVEMENT previousMove;
        do {
            previousMove = curMove;
            curMove = getNextMove(previousMove);
            System.out.println("Move: " + curMove.print(curMove));

            bot.move(curMove);

            if (exMap.getCell(bot.getRobotPosRow(), bot.getRobotPosCol()).getIsExplored()) {
                numOfContinuousExplored++;
            } else {
                numOfContinuousExplored = 0;
            }

            senseAndRepaint();
            areaExplored = calculateAreaExplored();
            System.out.println("Area explored: " + areaExplored);

            if (areaExplored == 300) {
                return;
            }
        }
        while (!(bot.getRobotPosRow() == r && bot.getRobotPosCol() == c) && areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime && numOfContinuousExplored <= 10);
    }

    /**
     * Returns the next move according to the current direction, neighbouring cells and previous move.
     */
    private MOVEMENT getNextMove(MOVEMENT previousMove) {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();

        System.out.println("Current robot position: (" + botRow + ", " + botCol + ")");
        System.out.println("Current robot direction: " + bot.getRobotCurDir());

        switch (bot.getRobotCurDir()) {
            case NORTH:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("N00");
                    return MOVEMENT.FORWARD;
                }

                if (northFree() && !westFree()) {
                    System.out.println("N01");
                    return MOVEMENT.FORWARD;
                }

                if (westFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("N02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("N03");
                    return MOVEMENT.FORWARD;
                }

                if (eastFree() && !northFree()) {
                    System.out.println("N04");
                    return MOVEMENT.RIGHT;
                }

                if (northFree()) {
                    System.out.println("N05");
                    return MOVEMENT.FORWARD;
                }

                System.out.println("N06");
                return MOVEMENT.RIGHT;


            case EAST:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("E00");
                    return MOVEMENT.FORWARD;
                }

                if (eastFree() && !northFree()) {
                    System.out.println("E01");
                    return MOVEMENT.FORWARD;

                }

                if (northFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("E02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("E03");
                    return MOVEMENT.FORWARD;
                }

                if (southFree() && !eastFree()) {
                    System.out.println("E04");
                    return MOVEMENT.RIGHT;
                }

                if (eastFree()) {
                    System.out.println("E05");
                    return MOVEMENT.FORWARD;
                }

                System.out.println("E06");
                return MOVEMENT.RIGHT;


            case SOUTH:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("S00");
                    return MOVEMENT.FORWARD;
                }

                if (southFree() && !eastFree()) {
                    System.out.println("S01");
                    return MOVEMENT.FORWARD;
                }

                if (eastFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("S02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("S03");
                    return MOVEMENT.FORWARD;
                }

                if (westFree() && !southFree()) {
                    System.out.println("S04");
                    return MOVEMENT.RIGHT;
                }

                if (southFree()) {
                    System.out.println("S05");
                    return MOVEMENT.FORWARD;
                }

                System.out.println("S06");
                return MOVEMENT.RIGHT;


            case WEST:
                if (northFree() && eastFree() && westFree() && southFree()) {
                    System.out.println("W00");
                    return MOVEMENT.FORWARD;
                }

                if (westFree() && !southFree()) {
                    System.out.println("W01");
                    return MOVEMENT.FORWARD;
                }

                if (southFree()) {
                    if (previousMove != MOVEMENT.LEFT) {
                        System.out.println("W02");
                        return MOVEMENT.LEFT;
                    }
                    System.out.println("W03");
                    return MOVEMENT.FORWARD;
                }

                if (northFree() && !westFree()) {
                    System.out.println("W04");
                    return MOVEMENT.RIGHT;
                }

                if (westFree()) {
                    System.out.println("W05");
                    return MOVEMENT.FORWARD;
                }

                System.out.println("W06");
                return MOVEMENT.RIGHT;


            default:
                return MOVEMENT.ERROR;
        }
    }

    /**
     * Finds the closest unexplored cell using the minimum values passed for row & column. Once found, finds the
     * closest explored cell to the closest unexplored cell and instructs the robot to navigate to it (if possible).
     */
    private void closestUnexploredCell(int minRow, int minCol) {
        for (int r = minRow; r < MapConstants.MAP_ROWS; r++) {
            for (int c = minCol; c < MapConstants.MAP_COLS; c++) {
                Cell unexploredCell = exMap.getCell(r, c);
                if (!unexploredCell.getIsExplored()) {
                    Cell nearestExploredCell = checkForNearestExploredCell(unexploredCell);
                    if (nearestExploredCell != null) {
                        System.out.println("Closest Unexplored Cell is (" + r + ", " + c + ")");
                        goToNearestExploredCell(unexploredCell, nearestExploredCell);
                    } else {
                        System.out.println("No explored cells near (" + r + ", " + c + ")");
                    }
                }
            }
        }
    }

    /**
     * Returns the nearest explored cell to the passed cell that is free to move into.
     */
    private Cell checkForNearestExploredCell(Cell c) {
        int c_row = c.getRow();
        int c_col = c.getCol();
        Cell nearestExploredCell = null;

        System.out.println("Checking for nearest explored cell for (" + c_row + ", " + c_col + ")");
        if (isExploredAndFree(c_row - 2, c_col)) {
            // South of unexplored cell
            nearestExploredCell = exMap.getCell(c_row - 2, c_col);
        } else if (isExploredAndFree(c_row + 2, c_col)) {
            // North of unexplored cell
            nearestExploredCell = exMap.getCell(c_row + 2, c_col);
        } else if (isExploredAndFree(c_row, c_col - 2)) {
            // West of unexplored cell
            nearestExploredCell = exMap.getCell(c_row, c_col - 2);
        } else if (isExploredAndFree(c_row, c_col + 2)) {
            // East of unexplored cell
            nearestExploredCell = exMap.getCell(c_row, c_col + 2);
        }

        return nearestExploredCell;
    }

    /**
     * Moves the robot to the nearest explored cell, turns it such that the unexplored cell is to the left and calls
     * the runExploration() method from this cell.
     */
    private void goToNearestExploredCell(Cell unexploredCell, Cell nearestExplorecCell) {
        int exploredRow = nearestExplorecCell.getRow();
        int exploredCol = nearestExplorecCell.getCol();
        int unexploredRow = unexploredCell.getRow();
        int unexploredCol = unexploredCell.getCol();

        DIRECTION direction = null;

        if (exploredRow == unexploredRow - 2) {
            // South of unexplored cell
            direction = DIRECTION.EAST;
        } else if (exploredRow == unexploredRow + 2) {
            // North of unexplored cell
            direction = DIRECTION.WEST;
        } else if (exploredCol == unexploredCol - 2) {
            // West of unexplored cell
            direction = DIRECTION.SOUTH;
        } else if (exploredCol == unexploredCol + 2) {
            // East of unexplored cell
            direction = DIRECTION.NORTH;
        }

        System.out.println("Going to (" + exploredRow + ", " + exploredCol + ") with direction " + direction);

        // Go to the nearest explored cell
        FastestPathAlgo fpa = new FastestPathAlgo(exMap, bot, realMap);
        Object success = fpa.runFastestPath(exploredRow, exploredCol);

        if (success == null) {
            closestUnexploredCell(unexploredRow, unexploredCol + 1);
        } else {
            areaExplored = calculateAreaExplored();
            turnBotDirection(direction);
            runExploration(exploredRow, exploredCol);
        }
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
            senseAndRepaint();
        } else if (numOfTurn == 2) {
            bot.move(MOVEMENT.RIGHT);
            senseAndRepaint();
            bot.move(MOVEMENT.RIGHT);
            senseAndRepaint();
        }

        System.out.println("Robot direction: " + bot.getRobotCurDir());
    }

    /**
     * Returns true if the robot can move to the north cell.
     */
    private boolean northFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow + 1, botCol - 1) && isExploredAndFree(botRow + 1, botCol) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the east cell.
     */
    private boolean eastFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol + 1) && isExploredAndFree(botRow, botCol + 1) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the south cell.
     */
    private boolean southFree() {
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow - 1, botCol) && isExploredNotObstacle(botRow - 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the west cell.
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

    /**
     * Sets the bot's sensors, processes the sensor data and repaints the map.
     */
    private void senseAndRepaint() {
        bot.setSensors();
        bot.sense(exMap, realMap);
        exMap.repaint();
    }
}

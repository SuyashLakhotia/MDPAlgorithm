package algorithms;

import map.Cell;
import map.Map;
import map.MapConstants;
import robot.Robot;
import robot.RobotConstants;
import robot.RobotConstants.MOVEMENT;
import robot.RobotConstants.DIRECTION;

import java.util.ArrayList;

/**
 * Exploration algorithm for the robot.
 *
 * @author Suyash Lakhotia
 */

public class ExplorationAlgo {
    private Map exMap, realMap;
    private Robot bot;
    private ArrayList<Cell> path= new ArrayList<>();
    private int areaExplored;
    private int[] sensorData;

    public ExplorationAlgo(Map exMap, Map realMap, Robot bot) {
        this.exMap = exMap;
        this.realMap = realMap;
        this.bot = bot;
    }

    public void runExploration() {
        bot.setSensors();
        sensorData = bot.sense(exMap, realMap);
        areaExplored = calculateAreaExplored();
        System.out.println("Explored Area: " + areaExplored);
        exMap.repaint();
        looping(RobotConstants.START_ROW, RobotConstants.START_COL);
        while(areaExplored!=300){
            System.out.println("exploration is still not complete");
            Cell closestUnexplored = closestUnexploredGrid();
            System.out.println("Closest Unexplored Grid is: " + closestUnexplored.getRow() + ", " + closestUnexplored.getCol());
            Cell nearbyObstacle = getNearbyObstacle(closestUnexplored);
            if(nearbyObstacle!=null){
                System.out.println("Nearby Obstacle: " + nearbyObstacle.getRow() + ", " + nearbyObstacle.getCol());
                getExploredCellNearOb(nearbyObstacle);
            }else{
                getExploredCellNearOb(closestUnexplored);
            }

        }
        System.out.println("Exploration of all grids complete");
        FastestPathAlgo returnToStart = new FastestPathAlgo(exMap, bot);
        returnToStart.runFastestPath(exMap,1, 1);

        turnBotDirection(DIRECTION.NORTH);

    }

    //moves the robot according to the left sticking rule starting from the cell(r,c) until the robot comes back
    // to the cell(r,c)
    private void looping(int r, int c){
        MOVEMENT nextMove = null;
        MOVEMENT previousMove;
        do{
            path.add(exMap.getCell(bot.getRobotPosRow(), bot.getRobotPosCol()));
            previousMove = nextMove;
            nextMove = getNextMove(previousMove);
            System.out.println("move: "+nextMove);
            bot.move(nextMove);
            bot.setSensors();
            sensorData = bot.sense(exMap, realMap);
            areaExplored = calculateAreaExplored();
            System.out.println("Area explored: "+areaExplored);
            exMap.repaint();
            if(areaExplored == 300){
                return;
            }
        }while(bot.getRobotPosRow()!=r || bot.getRobotPosCol()!=c);
    }

    public void runExploration(int timeLimit) {// timelimit is in seconds
        long start = System.currentTimeMillis();
        long end = start + timeLimit*1000; // 60 seconds * 1000 ms/sec
        MOVEMENT nextMove = null;
        MOVEMENT prevMov;
        bot.setSensors();
        sensorData = bot.sense(exMap, realMap);
        areaExplored = calculateAreaExplored();
        System.out.println("Explored area: " + areaExplored);
        exMap.repaint();
        do{
            prevMov = nextMove;
            nextMove = getNextMove(prevMov);
            System.out.println("move: " + nextMove);
            bot.move(nextMove);
            bot.setSensors();
            sensorData = bot.sense(exMap, realMap);
            areaExplored = calculateAreaExplored();
            System.out.println("Explored area: " + areaExplored);
            exMap.repaint();
        }while((bot.getRobotPosCol() != 1 || bot.getRobotPosRow() != 1) && (System.currentTimeMillis() < end));

        if (areaExplored!=300){
            System.out.println("Grid not explored entirely");
        }

        // go to the start position
        FastestPathAlgo goToStart = new FastestPathAlgo(exMap,bot);
        goToStart.runFastestPath(exMap,1,1);

        //after back to the start zone
        //turn to North (Ready for shortest path finding)
        while(bot.getRobotCurDir() != DIRECTION.NORTH){
            bot.move(MOVEMENT.RIGHT);
        }
    }

    public void runExploration(long coverageLimit) {
        MOVEMENT nextMove = null;
        MOVEMENT prevMov;
        bot.setSensors();
        sensorData = bot.sense(exMap, realMap);
        areaExplored = calculateAreaExplored();
        System.out.println("Explored area: " + areaExplored);
        exMap.repaint();
        do{
            prevMov = nextMove;
            nextMove = getNextMove(prevMov);
            System.out.println("move: " + nextMove);
            bot.move(nextMove);
            bot.setSensors();
            sensorData = bot.sense(exMap, realMap);
            areaExplored = calculateAreaExplored();
            System.out.println("exploredArea: " + areaExplored);
            exMap.repaint();
        }while((bot.getRobotPosCol() != 1 || bot.getRobotPosRow() != 1) && (areaExplored < (coverageLimit * MapConstants.MAP_SIZE /100)));

        if (areaExplored!=300){
            System.out.println("there are still unexplored areas!!!!");
        }

        //go back to start zone
        FastestPathAlgo goToStart = new FastestPathAlgo(exMap,bot);
        goToStart.runFastestPath(exMap,1,1);

        //after back to the start zone
        //turn to North (Ready for shortest path finding)
        while(bot.getRobotCurDir() != DIRECTION.NORTH){
            bot.move(MOVEMENT.RIGHT);
        }
    }

    private MOVEMENT getNextMove(MOVEMENT previousMove){
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        System.out.println("current robot position: " + botRow +", " + botCol);
        System.out.println("current robot direction: " + bot.getRobotCurDir());
        switch(bot.getRobotCurDir()){
            case NORTH:
                if(northFree() && !westFree()){
                    return MOVEMENT.FORWARD;
                }
                else if(westFree()){
                    if(previousMove!=MOVEMENT.LEFT){
                        return MOVEMENT.LEFT;
                    }
                    return MOVEMENT.FORWARD;
                }
                else if(eastFree() && !northFree()){
                    return MOVEMENT.RIGHT;
                }
                else{
                    System.out.println("north error");
                    if(northFree()){
                        return MOVEMENT.FORWARD;
                    }
                    return MOVEMENT.RIGHT;
                }

            case EAST:
                if(eastFree() && !northFree()){
                    return MOVEMENT.FORWARD;
                }
                else if(northFree()){
                    if(previousMove!=MOVEMENT.LEFT){
                        return MOVEMENT.LEFT;
                    }
                    return MOVEMENT.FORWARD;
                }
                else if(southFree() && !eastFree()){
                    return MOVEMENT.RIGHT;
                }
                else{
                    System.out.println("east error");
                    if(eastFree()){
                        return MOVEMENT.FORWARD;
                    }
                    return MOVEMENT.RIGHT;
                }

            case SOUTH:
                if(southFree() && !eastFree()){
                    return MOVEMENT.FORWARD;
                }
                else if(eastFree()){
                    if(previousMove!=MOVEMENT.LEFT){
                        return MOVEMENT.LEFT;
                    }
                    return MOVEMENT.FORWARD;
                }
                else if(westFree() && !southFree()){
                    return MOVEMENT.RIGHT;
                }
                else{
                    System.out.println("south error");
                    if(southFree()){
                        return MOVEMENT.FORWARD;
                    }
                    return MOVEMENT.RIGHT;
                }

            case WEST:
                if(westFree() && !southFree()){
                    return MOVEMENT.FORWARD;
                }
                else if(southFree()){
                    if(previousMove!=MOVEMENT.LEFT){
                        return MOVEMENT.LEFT;
                    }
                    return MOVEMENT.FORWARD;
                }
                else if(northFree() && !westFree()){
                    return MOVEMENT.RIGHT;
                }
                else{
                    System.out.println("west error");
                    if(westFree()){
                        return MOVEMENT.FORWARD;
                    }
                    return MOVEMENT.RIGHT;
                }

            default:
                System.out.println("default error!");
                return MOVEMENT.FORWARD;
        }
    }


    private Cell closestUnexploredGrid(){
        Cell rCell = closestRowUnexploredGrid();
        Cell cCell = closestColUnexploredGrid();
        int rDis = Math.abs(rCell.getRow() - bot.getRobotPosRow()) + Math.abs(rCell.getCol() - bot.getRobotPosCol());
        int cDis = Math.abs(cCell.getRow() - bot.getRobotPosRow()) + Math.abs(cCell.getCol() - bot.getRobotPosCol());
        if (rDis < cDis){
            return rCell;
        }
        return cCell;

    }
    private Cell closestRowUnexploredGrid(){
        for (int r=0;r<MapConstants.MAP_ROWS;r++){
            for (int c=0; c<MapConstants.MAP_COLS; c++){
                if (!exMap.getCell(r,c).getIsExplored()){
                    return exMap.getCell(r,c);
                }
            }
        }
        return null;
    }

    private Cell closestColUnexploredGrid(){
        for (int c=0; c<MapConstants.MAP_COLS; c++){
            for (int r=0;r<MapConstants.MAP_ROWS;r++){
                if (!exMap.getCell(r,c).getIsExplored()){
                    return exMap.getCell(r,c);
                }
            }
        }
        return null;
    }

    private Cell getNearbyObstacle(Cell blk){
        int c = blk.getCol();
        int r = blk.getRow();
        if (exMap.getCell(r,c+1).getIsObstacle()){
            return exMap.getCell(r,c+1);
        }
        else if (exMap.getCell(r,c-1).getIsObstacle()){
            return exMap.getCell(r,c-1);
        }
        else if (exMap.getCell(r+1,c).getIsObstacle()){
            return exMap.getCell(r+1,c);
        }
        else if (exMap.getCell(r-1,c).getIsObstacle()){
            return exMap.getCell(r-1,c);
        }
        else{
            return null;
        }
    }

    private void turnBotDirection(DIRECTION dir){
        System.out.println("robot direction: " + bot.getRobotCurDir());
        while(bot.getRobotCurDir() != dir){
            bot.move(MOVEMENT.RIGHT);
            bot.setSensors();
            sensorData = bot.sense(exMap, realMap);
            exMap.repaint();
            System.out.println("robot direction: " + bot.getRobotCurDir());
        }
    }

    private Cell getExploredCellNearOb(Cell obstacle){
        int ob_row = obstacle.getRow();
        int ob_col = obstacle.getCol();

        Cell cellNearOb = null;
        DIRECTION direction = null;
        if (isFreeToGo(ob_row-2,ob_col)){
            //south of the obstacle facing east
            cellNearOb = exMap.getCell(ob_row-2,ob_col);
            direction = DIRECTION.EAST;
        }
        else if (isFreeToGo(ob_row+2, ob_col)){
            //north of the obstacle facing west
            cellNearOb = exMap.getCell(ob_row+2,ob_col);
            direction = DIRECTION.WEST;
        }
        else if (isFreeToGo(ob_row,ob_col-2)){
            //west of the obstacle facing south
            cellNearOb = exMap.getCell(ob_row,ob_col-2);
            direction = DIRECTION.SOUTH;
        }
        else if (isFreeToGo(ob_row,ob_col+2)){
            //east of the obstacle facing north
            cellNearOb = exMap.getCell(ob_row,ob_col+2);
            direction = DIRECTION.NORTH;
        }
        // go to the mark point
        FastestPathAlgo fpa = new FastestPathAlgo(exMap,bot,realMap);
        fpa.runFastestPath(exMap,cellNearOb.getRow(),cellNearOb.getCol());
        // bot.setSensors();
        // sensorData = bot.sense(exMap, realMap);
        areaExplored = calculateAreaExplored();
        turnBotDirection(direction);
        System.out.println("current position of the bot: " + bot.getRobotPosRow() +", " + bot.getRobotPosCol());
        System.out.println("Nearby explored cell: " + cellNearOb.getRow() + ", " + cellNearOb.getCol());
        looping(cellNearOb.getRow(),cellNearOb.getCol());
        return cellNearOb;
    }

    // return true if b is not a virtual wall nor obstacle and alr explored
    private boolean isFreeToGo(int r, int c){
        if (r>=0 && r<MapConstants.MAP_ROWS && c>=0 && c<MapConstants.MAP_COLS){
            Cell b = exMap.getCell(r,c);
            return (b.getIsExplored() && !b.getIsVirtualWall() && !b.getIsObstacle());
        }
        return false;
    }



    //return true if its explored and its not a obstacle
    private boolean checkFree(int r, int c){
        boolean isFree = false;
        if (r>=0 && r<MapConstants.MAP_ROWS && c>=0 && c<MapConstants.MAP_COLS){
            isFree = (exMap.getCell(r,c).getIsExplored() && (!exMap.getCell(r,c).getIsObstacle())); //explored and not obstacle
        }

        return isFree;
    }

    //return true if west side is free
    private boolean westFree(){
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return(checkFree(botRow-1, botCol-2) && checkFree(botRow, botCol-2) && checkFree(botRow+1, botCol-2));
    }
    //return true if east side is free
    private boolean eastFree(){
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (checkFree(botRow-1, botCol+2) && checkFree(botRow, botCol+2) && checkFree(botRow+1, botCol+2));
    }
    //return true if north side is free
    private boolean northFree(){
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (checkFree(botRow+2, botCol - 1) && checkFree(botRow+2, botCol + 1) && checkFree(botRow+2, botCol));
    }
    //return true if south side is free
    private boolean southFree(){
        int botRow = bot.getRobotPosRow();
        int botCol = bot.getRobotPosCol();
        return (checkFree(botRow-2, botCol-1) && checkFree(botRow-2, botCol) && checkFree(botRow-2, botCol+1));
    }



    private int calculateAreaExplored(){
        int result = 0;
        for(int r = 0;r< MapConstants.MAP_ROWS;r++){
            for(int c= 0;c<MapConstants.MAP_COLS;c++){
                if(exMap.getCell(r,c).getIsExplored()){
                    result++;
                }
            }
        }
        return result;
    }


}

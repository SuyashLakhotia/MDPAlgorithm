package map;

/**
 * Represents each cell in the map grid.
 *
 * @author Suyash Lakhotia
 */

public class Cell {
    private int row;
    private int col;
    private boolean isObstacle;
    private boolean isVirtualWall;
    private boolean isExplored;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public void setIsObstacle(boolean val) {
        this.isObstacle = val;
    }

    public boolean getIsObstacle() {
        return this.isObstacle;
    }

    public void setVirtualWall(boolean val) {
        this.isVirtualWall = val;
    }

    public boolean getIsVirtualWall() {
        return this.isVirtualWall;
    }

    public void setIsExplored(boolean val) {
        this.isExplored = val;
    }

    public boolean getIsExplored() {
        return this.isExplored;
    }
}

package map;

import java.awt.*;

/**
 * Constants used in the Map class.
 *
 * @author Suyash Lakhotia
 */

public class MapConstants {
    public static final int MAP_SIZE = 300;     // total num of cells
    public static final int MAP_ROWS = 20;      // total num of rows
    public static final int MAP_COLS = 15;      // total num of cols
    public static final int GOAL_ROW = 18;      // row no. of goal cell
    public static final int GOAL_COL = 13;      // col no. of goal cell

    public static final Color C_BORDER = Color.BLACK;
    public static final Color C_BORDER_WARNING = new Color(255, 102, 153, 200);

    public static final Color C_GRID_LINE = Color.ORANGE;
    public static final int GRID_LINE_WEIGHT = 2;

    public static final Color C_START = Color.BLUE;
    public static final Color C_GOAL = Color.GREEN;
    public static final Color C_UNEXPLORED = Color.LIGHT_GRAY;
    public static final Color C_FREE = Color.WHITE;
    public static final Color C_OBSTACLE = Color.DARK_GRAY;

    public static final Color C_ROBOT = Color.RED;
    public static final Color C_ROBOT_DIR = new Color(0, 46, 155, 220);

    public static final int CELL_SIZE = 30; // cell size for rendering
}

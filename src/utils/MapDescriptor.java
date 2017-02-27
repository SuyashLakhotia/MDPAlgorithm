package utils;

import map.Map;
import map.MapConstants;

import java.io.*;

/**
 * Helper methods for reading & generating map strings.
 *
 * @author Suyash Lakhotia
 */

public class MapDescriptor {
    /**
     * Reads filename.txt from disk and loads it into the passed Map object. Uses a simple binary indicator to
     * identify if a cell is an obstacle.
     */
    public static void readMapString(Map map, String filename) {
        try {
            InputStream inputStream = new FileInputStream(filename + ".txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = buf.readLine();
            }

            String bin = sb.toString();
            int binPtr = 0;
            for (int row = MapConstants.MAP_ROWS - 1; row >= 0; row--) {
                for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                    if (bin.charAt(binPtr) == '1') map.setObstacleCell(row, col, true);
                    binPtr++;
                }
            }

            map.setAllExplored();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String binToHex(String bin) {
        int dec = Integer.parseInt(bin, 2);
        String hex = Integer.toHexString(dec);

        return hex;
    }

    /**
     * Generates Part 1 & Part 2 map descriptor strings from the passed Map object.
     */
    public static void generateMapDescriptor(Map map) {
        StringBuilder Part1 = new StringBuilder();
        StringBuilder Part1_bin = new StringBuilder();
        Part1_bin.append("11");
        for (int r = 0; r < MapConstants.MAP_ROWS; r++) {
            for (int c = 0; c < MapConstants.MAP_COLS; c++) {
                if (map.getCell(r, c).getIsExplored())
                    Part1_bin.append("1");
                else
                    Part1_bin.append("0");

                if (Part1_bin.length() == 4) {
                    Part1.append(binToHex(Part1_bin.toString()));
                    Part1_bin.setLength(0);
                }
            }
        }
        Part1_bin.append("11");
        Part1.append(binToHex(Part1_bin.toString()));
        System.out.println("P1: " + Part1.toString());

        StringBuilder Part2 = new StringBuilder();
        StringBuilder Part2_bin = new StringBuilder();
        for (int r = 0; r < MapConstants.MAP_ROWS; r++) {
            for (int c = 0; c < MapConstants.MAP_COLS; c++) {
                if (map.getCell(r, c).getIsExplored()) {
                    if (map.getCell(r, c).getIsObstacle())
                        Part2_bin.append("1");
                    else
                        Part2_bin.append("0");

                    if (Part2_bin.length() == 4) {
                        Part2.append(binToHex(Part2_bin.toString()));
                        Part2_bin.setLength(0);
                    }
                }
            }
        }
        if (Part2_bin.length() > 0) Part2.append(binToHex(Part2_bin.toString()));
        System.out.println("P2: " + Part2.toString());
    }
}

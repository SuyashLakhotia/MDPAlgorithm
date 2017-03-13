package utils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Communication manager to communicate with the different parts of the system via the RasPi.
 *
 * @author SuyashLakhotia
 */

public class CommMgr {
    private final String HOST = "192.168.2.1";
    private final int PORT = 8008;

    public static final String START = "PC_START";      // Android --> PC
    public static final String BOT_START = "BOT_START"; // PC --> Arduino
    public static final String SENSOR_DATA = "SDATA";   // Arduino --> PC
    public static final String INSTRUCTIONS = "INSTR";  // PC --> Arduino
    public static final String MAP_STRINGS = "MAP";      // PC --> Android
    public static final String BOT_POS = "BOT_POS";     // PC --> Android

    private static CommMgr commMgr = null;
    private static Socket conn = null;

    private BufferedWriter writer;
    private BufferedReader reader;

    private CommMgr() {
    }

    public static CommMgr getCommMgr() {
        if (commMgr == null) {
            commMgr = new CommMgr();
        }
        return commMgr;
    }

    public void openConnection() {
        System.out.println("Opening connection...");

        try {
            conn = new Socket(HOST, PORT);

            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(conn.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            System.out.println("openConnection() --> " + "Connection established successfully!");

            return;
        } catch (UnknownHostException e) {
            System.out.println("openConnection() --> UnknownHostException");
        } catch (IOException e) {
            System.out.println("openConnection() --> IOException");
        } catch (Exception e) {
            System.out.println("openConnection() --> Exception");
            System.out.println(e.toString());
        }

        System.out.println("Failed to establish connection!");
    }

    public void closeConnection() {
        System.out.println("Closing connection...");

        try {
            reader.close();

            if (conn != null) {
                conn.close();
                conn = null;
            }
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.out.println("closeConnection() --> IOException");
        } catch (NullPointerException e) {
            System.out.println("closeConnection() --> NullPointerException");
        } catch (Exception e) {
            System.out.println("closeConnection() --> Exception");
            System.out.println(e.toString());
        }
    }

    public void sendMsg(String msg, String msgType) {
        System.out.println("Sending a message...");

        try {
            String outputMsg;
            if (msg == null) {
                outputMsg = msgType + "\n";
            } else if (msgType.equals(MAP_STRINGS) || msgType.equals(BOT_POS)) {
                outputMsg = msgType + " " + msg + "\n";
            } else {
                outputMsg = msgType + "\n" + msg + "\n";
            }

            System.out.println("Sending out message:\n" + outputMsg);
            writer.write(outputMsg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("sendMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("sendMsg() --> Exception");
            System.out.println(e.toString());
        }
    }

    public String recvMsg() {
        System.out.println("Receiving a message...");

        try {
            StringBuilder sb = new StringBuilder();
            String input = reader.readLine();

            if (input != null && input.length() > 0) {
                if (input.split(";")[0].equals(START)) {
                    sb.append(input);
                } else if (input.split(";")[0].equals(SENSOR_DATA)) {
                    sb.append(input);
                }

                System.out.println(sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            System.out.println("recvMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("recvMsg() --> Exception");
            System.out.println(e.toString());
        }

        return null;
    }

    public boolean isConnected() {
        return conn.isConnected();
    }
}

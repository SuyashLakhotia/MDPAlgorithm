package simulator;

import algorithms.ExplorationAlgo;
import algorithms.FastestPathAlgo;
import map.Map;
import robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static utils.MapDescriptor.generateMapDescriptor;
import static utils.MapDescriptor.readMapString;

/**
 * Simulator for robot navigation in virtual arena.
 *
 * @author Suyash Lakhotia
 */

public class Simulator {
    // JFrame for the application
    private static JFrame _appFrame = null;

    // JPanel for laying out different maps
    private static JPanel _mapCards = null;

    // JPanel for main menu buttons
    private static JPanel _buttons = null;

    private static Robot bot;

    private static Map realMap = null;
    private static Map exploredMap = null;
    private static Map timeExploredMap = null;
    private static Map coverageExploredMap = null;

    // Time-limited Exploration
    private static int timeLimit = 10;

    // Coverage-limited Exploration
    private static long coverageLimit = 0;

    public static void main(String[] args) {
        bot = new Robot(1, 1);
        realMap = new Map(bot);

        exploredMap = new Map(bot);
        exploredMap.setAllUnexplored();

        timeExploredMap = new Map(bot);
        timeExploredMap.setAllUnexplored();

        coverageExploredMap = new Map(bot);
        coverageExploredMap.setAllUnexplored();

        displayEverything();
    }

    private static void displayEverything() {
        // Main frame for displaying everything
        _appFrame = new JFrame();
        _appFrame.setTitle("MDP Group 2 Simulator");
        _appFrame.setSize(new Dimension(690, 700));
        _appFrame.setResizable(false);

        // Center the main frame in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        _appFrame.setLocation(dim.width / 2 - _appFrame.getSize().width / 2, dim.height / 2 - _appFrame.getSize().height / 2);

        // Create the CardLayout for storing the different maps
        _mapCards = new JPanel(new CardLayout());

        // Create the JPanel for the buttons
        _buttons = new JPanel();

        // Add _mapCards & _buttons to content pane
        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);
        contentPane.add(_buttons, BorderLayout.PAGE_END);

        // Initialize the main map view
        initMainLayout();

        // Initialize the buttons
        initButtonsLayout();

        // Display the application
        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void initMainLayout() {
        _mapCards.add(realMap, "REAL_MAP");
        _mapCards.add(exploredMap, "EXPLORATION");
        _mapCards.add(timeExploredMap, "TIME_EXPLORATION");
        _mapCards.add(coverageExploredMap, "COVERAGE_EXPLORATION");

        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        cl.show(_mapCards, "REAL_MAP");
    }

    private static void initButtonsLayout() {
        _buttons.setLayout(new GridLayout());
        addButtons();
    }

    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    private static void addButtons() {
        // Load Map Button
        JButton btn_LoadMap = new JButton("Load Map");
        formatButton(btn_LoadMap);
        btn_LoadMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog loadMapDialog = new JDialog(_appFrame, "Load Map", true);
                loadMapDialog.setSize(400, 60);
                loadMapDialog.setLayout(new FlowLayout());

                final JTextField loadTF = new JTextField(15);
                JButton loadMapButton = new JButton("Load");

                loadMapButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        readMapString(realMap, loadTF.getText());
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "REAL_MAP");
                        realMap.repaint();
                        generateMapDescriptor(realMap);
                        loadMapDialog.setVisible(false);
                    }
                });

                loadMapDialog.add(new JLabel("File Name: "));
                loadMapDialog.add(loadTF);
                loadMapDialog.add(loadMapButton);
                loadMapDialog.setVisible(true);
            }
        });
        _buttons.add(btn_LoadMap);


        // Exploration Class for Multithreading
        class Exploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(1, 1);
                exploredMap.repaint();

                ExplorationAlgo exploration = new ExplorationAlgo(exploredMap, realMap, bot);
                exploration.runExploration();

                generateMapDescriptor(exploredMap);

                return 111;
            }
        }

        // Exploration Button
        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "EXPLORATION");
                new Exploration().execute();
            }
        });
        _buttons.add(btn_Exploration);


        // FastestPath Class for Multithreading
        class FastestPath extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(1, 1);
                realMap.repaint();

                FastestPathAlgo fastestPath = new FastestPathAlgo(realMap, bot);
                StringBuilder output = fastestPath.runFastestPath(realMap, 18, 13);
                byte[] outputByteArray = String.valueOf(output).getBytes();
                System.out.println(outputByteArray);

                return 222;
            }
        }

        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "EXPLORATION");
                new FastestPath().execute();
            }
        });
        _buttons.add(btn_FastestPath);


        // TimeExploration Class for Multithreading
        class TimeExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(1, 1);
                timeExploredMap.repaint();

                ExplorationAlgo timeExpo = new ExplorationAlgo(timeExploredMap, realMap, bot);
                timeExpo.runExploration(timeLimit);

                return 333;
            }
        }

        // Time-limited Exploration Button
        JButton btn_TimeExploration = new JButton("Time-Limited");
        formatButton(btn_TimeExploration);
        btn_TimeExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog timeExploDialog = new JDialog(_appFrame, "Time-Limited Exploration", true);
                timeExploDialog.setSize(400, 60);
                timeExploDialog.setLayout(new FlowLayout());
                final JTextField timeTF = new JTextField(5);
                JButton timeSaveButton = new JButton("Run");

                timeSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        timeLimit = (Integer.parseInt(timeTF.getText()));
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "TIME_EXPLORATION");
                        new TimeExploration().execute();
                    }
                });

                timeExploDialog.add(new JLabel("Time Limit (in seconds): "));
                timeExploDialog.add(timeTF);
                timeExploDialog.add(timeSaveButton);
                timeExploDialog.setVisible(true);
            }
        });
        _buttons.add(btn_TimeExploration);


        // CoverageExploration Class for Multithreading
        class CoverageExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(1, 1);
                coverageExploredMap.repaint();

                ExplorationAlgo coverageExpo = new ExplorationAlgo(coverageExploredMap, realMap, bot);
                coverageExpo.runExploration(coverageLimit);

                generateMapDescriptor(coverageExploredMap);

                return 444;
            }
        }

        // Coverage-limited Exploration Button
        JButton btn_CoverageExploration = new JButton("Coverage-Limited");
        formatButton(btn_CoverageExploration);
        btn_CoverageExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog coverageExploDialog = new JDialog(_appFrame, "Coverage-Limited Exploration", true);
                coverageExploDialog.setSize(400, 60);
                coverageExploDialog.setLayout(new FlowLayout());
                final JTextField coverageTF = new JTextField(5);
                JButton coverageSaveButton = new JButton("Run");

                coverageSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        coverageLimit = (Integer.parseInt(coverageTF.getText()));
                        new CoverageExploration().execute();
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "COVERAGE_EXPLORATION");
                    }
                });

                coverageExploDialog.add(new JLabel("Coverage Limit (% of maze): "));
                coverageExploDialog.add(coverageTF);
                coverageExploDialog.add(coverageSaveButton);
                coverageExploDialog.setVisible(true);
            }
        });
        _buttons.add(btn_CoverageExploration);
    }
}

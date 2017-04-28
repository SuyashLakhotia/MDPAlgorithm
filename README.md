# MDP Algorithm

This repository contains the algorithm(s) used to drive an autonomous ground vehicle through an unknown maze. It is part of a bigger project that involved a physical robot (Arduino Uno + Hardware), an Android tablet and a Raspberry Pi (communication server).

There are two main driving phases &mdash; **Exploration** & **Fastest Path**. The exploration phase involves the robot exploring the unknown maze to detect the locations of obstacles, starting from the `START` zone and ending at the `START` zone. The fastest path phase involves the robot computing and executing the fastest path from the `START` zone to the `GOAL` zone.

### Running MDPAlgorithm

In order to run this project independently (i.e. without the other components of the system, including the physical robot), the built-in simulator can be used.

Set `realRun` in Line 40 of `Simulator.java` to `false`:

```java
private static final boolean realRun = false;
```

Run `Simulator.java` and load a map by clicking on the `Load Map` button and typing in the file name of the map (without the `.txt` extension) you wish to load. The current available maps are inside `maps/`. Once a map is loaded, use the `Exploration` button to start the exploration of the map and the `Fastest Path` button to compute and execute the fastest path from `START` to `GOAL`.

> To make your own map, simply copy the contents of `BlankMap.txt` and encode obstacles in the map by replacing a `0` cell with a `1` cell. Note that the `START` and `GOAL` zones cannot have obstacles in them.

### Demos

#### Exploration

<img src="Exploration Demo.gif" height="500px" width="auto"/>

#### Fastest Path

<img src="Fastest Path Demo.gif" height="500px" width="auto"/>

****

***Disclaimer:*** This repo is no longer maintained and was submitted as part of the coursework for CE/CZ 3004 Multi-Disciplinary Project at NTU in AY 16/17 Semester 2.

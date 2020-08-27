import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;

import javax.annotation.processing.SupportedOptions;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ShadowDefend, a tower defence game.
 */
public class ShadowDefend extends AbstractGame {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    private static final String MAP_FILE = "res/levels/1.tmx";

    // Change to suit system specifications. This could be
    // dynamically determined but that is out of scope. Taken from given working implementation of Project 1.
    public static final double FPS = 60;
    // The spawn delay (in seconds) to spawn slicers
    //Taken from given working implementation of Project 1.
    private static final int INITIAL_TIMESCALE = 1;
    // Timescale is made static because it is a universal property of the game and the specification
    // says everything in the game is affected by this. Taken from given working implementation of Project 1.
    private static int timescale = INITIAL_TIMESCALE;

    private TiledMap map;
    private List<Point> polyline;

    //List of Sprites to accommodate for any type of slicer
    private static List<Sprite> slicers;
    private static List<Point> slicerLocation = new ArrayList<>();

    private double frameCount;
    private boolean waveStarted;

    //Starting variables
    private int level = 1;
    private static Integer money;
    private static String status = "Awaiting Start";
    private static int lives = 25;

    //Max levels in the game
    private static final int LEVELS = 2;

    //Variables that change according to the wave events in waves.txt file
    private int spawnedSlicers;
    private static int numberOfSlicers;
    public static int waveNumber = 0;
    private static String slicerType;
    private static String eventType;
    private static double delayBetweenSlicers;
    private static double delayTime;
    private static int currentWaveNumber = 0;

    //Store all information about wave events, where each String is a line from the waves.txt file
    private static List<String> waveEvents = new ArrayList<>();

    //Whether need to get information from next String in waveEvents or not
    private boolean getNextWave = true;
    //Whether wave is finished and reward needs to be given
    private boolean getReward = false;
    private int reward;

    //For use between this class and BuyPanel class for when towers are being placed
    private List<String> towerTypes = new ArrayList<>();
    private List<Sprite> towers = new ArrayList<>();
    private List<Boolean> isTarget = new ArrayList<Boolean>();
    private static List<Point> targetPoints = new ArrayList<>();

    private static List<Projectile> projectiles = new ArrayList<>();

    /**
     * Creates a new instance of the ShadowDefend game
     */
    public ShadowDefend() {
        super(WIDTH, HEIGHT, "ShadowDefend");

        this.map = new TiledMap(MAP_FILE);
        this.polyline = map.getAllPolylines().get(0);

        this.slicers = new ArrayList<>();
        this.spawnedSlicers = 0;
        this.waveStarted = false;
        this.frameCount = Integer.MAX_VALUE;
        this.money = 500;

        // Temporary fix for the weird slicer map glitch (might have to do with caching textures)
        // This fix is entirely optional. Taken from given working implementation of Project 1.
        new RegularSlicer(polyline, spawnedSlicers, waveNumber);
        new SuperSlicer(polyline, spawnedSlicers, waveNumber);
        new MegaSlicer(polyline, spawnedSlicers, waveNumber);
        new ApexSlicer(polyline, spawnedSlicers, waveNumber);
    }

    /**
     * The entry-point for the game
     * @param args Optional command-line arguments
     */
    public static void main(String[] args) {
        //Initialize variables needed for determining components of the wave
        //using the first line of the waveEvents ArrayList taken from waves.txt file
        getWaveEvents();
        new ShadowDefend().run();
    }

    /**
     * Get all lines from waves.txt file and input them into the ArrayList of waveEvents
     */
    private static void getWaveEvents() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/levels/waves.txt"));
            String content;
            while ((content = reader.readLine()) != null) {
                waveEvents.add(content);
            }
        } catch (FileNotFoundException e) {
            System.out.println("e");
        } catch (IOException e){
            System.out.println("e");
        }
    }

    //Taken from given working implementation of Project 1.
    public static int getTimescale() {
        return timescale;
    }

    /**
     * Increases the timescale
     * Taken from given working implementation of Project 1.
     */
    private void increaseTimescale() {
        timescale++;
    }

    /**
     * Decreases the timescale but doesn't go below the base timescale
     * Taken from given working implementation of Project 1.
     */
    private void decreaseTimescale() {
        if (timescale > INITIAL_TIMESCALE) {
            timescale--;
        }
    }

    /**
     * Calculates total penalty for the current slicer
     * @param spawnNumber of slicer
     * @param numberOfSlicers in the wave event
     * @param penalty for the slicer based on type (e.g. Megaslicer penalty = 4)
     * @return totalPenalty for slicer
     */
    public int calcPenalty(int spawnNumber, int numberOfSlicers, int penalty) {
        int totalPenalty;
        //total penalty is sum of child slicer penalties, find amount of children and multiply by penalty
        totalPenalty = (numberOfSlicers - spawnNumber) * penalty;
        return totalPenalty;
    }

    public static List<Point> getTargetPoints() {
        return targetPoints;
    }

    /**
     * Update the state of the game, potentially reading from input
     * Logic partially taken from given working implementation of Project 1.
     * @param input The current mouse/keyboard state
     */
    @Override
    protected void update(Input input) {
        // Increase the frame counter by the current timescale
        frameCount += getTimescale();

        // Draw map from the top left of the window based on current level
        String mapFile = String.format("res/levels/%d.tmx", level);
        map = new TiledMap(mapFile);
        polyline = map.getAllPolylines().get(0);
        map.draw(0,0,0,0, WIDTH, HEIGHT);

        //Draw the panels and update the values displayed
        BuyPanel bp = new BuyPanel(money, towerTypes, towers, polyline, map);
        StatusPanel sp = new StatusPanel(waveNumber, timescale, status, lives);


        //Get reward for the wave that just passed
        //Only passed if the slicer array has no more slicers with a waveNumber equal to the wave
//        System.out.print(waveNumber);
//        System.out.print(currentWaveNumber);
//        System.out.println(getReward);


//        if (getReward) {
//            boolean ready = true;
//            for (int i = 0; i < slicers.size(); i++) {
//                Sprite s = slicers.get(i);
//                if (s.getType().equals("slicer")) {
//                    RegularSlicer n = (RegularSlicer) s;
//                    if (n.getWaveNumber() == currentWaveNumber) {
//                        ready = false;
//                    }
//                } else if (s.getType().equals("superslicer")) {
//                    SuperSlicer n = (SuperSlicer) s;
//                    if (n.getWaveNumber() == currentWaveNumber) {
//                        ready = false;
//                    }
//                } else if (s.getType().equals("megaslicer")) {
//                    MegaSlicer n = (MegaSlicer) s;
//                    if (n.getWaveNumber() == currentWaveNumber) {
//                        ready = false;
//                    }
//                } else if (s.getType().equals("apexslicer")) {
//                    ApexSlicer n = (ApexSlicer) s;
//                    if (n.getWaveNumber() == currentWaveNumber) {
//                        ready = false;
//                    }
//                }
//            }
//            if (ready) {
//                int amount = money + 150 + (currentWaveNumber * 100);
//                bp.setMoney(amount);
//                getReward = false;
//                currentWaveNumber++;
//            }
//        }
        if (getReward) {
            bp.setMoney(reward);
            currentWaveNumber++;
            getReward = false;
        }

        money = bp.update(input);
        sp.update(input);
        towers = bp.getArray();
        towerTypes = bp.getTowerArray();

        //Change values of variables according to new wave event
        if (getNextWave == true) {
            //reset frameCount for new wave and reset spawnedSlicer count
            frameCount = 0;
            spawnedSlicers = 0;

            String str = waveEvents.get(0);
            String[] info = str.split(",");

            waveNumber = Integer.parseInt(info[0]);

            if (waveNumber != currentWaveNumber && currentWaveNumber != 0) {
                getReward = true;
                reward = money + 150 + (currentWaveNumber * 100);
            }
            if (currentWaveNumber == 0) {
                currentWaveNumber = waveNumber;
            }

            eventType = info[1];
            if (eventType.equals("spawn")) {
                numberOfSlicers = Integer.parseInt(info[2]);
                slicerType = info[3];
                delayBetweenSlicers = Double.parseDouble(info[4])/1000;
            } else if (eventType.equals("delay")){
                delayTime = Double.parseDouble(info[2])/1000;
            }

            waveEvents.remove(0);
            if (!waveEvents.isEmpty()) {
                reward = money + 150 + (waveNumber * 100);
            }
            getNextWave = false;
            return;
        }

        // Handle key presses. Taken from given working implementation of Project 1.
        if (waveStarted == false) {
            if (input.wasPressed(Keys.S)) {
                waveStarted = true;
            }
        }

        if (waveStarted == true) {
            if (eventType.equals("spawn")) {
                status = "Wave in Progress";
            }
        }
        if (input.wasPressed(Keys.L)) {
            increaseTimescale();
        }
        if (input.wasPressed(Keys.K)) {
            decreaseTimescale();
        }

        // Check if it is time to spawn a new slicer (and we have some left to spawn).
        // Taken from given working implementation of Project 1.
        if (waveStarted && eventType.equals("spawn") && frameCount / FPS >= delayBetweenSlicers
                && spawnedSlicers != numberOfSlicers) {
            status = "Wave in Progress";
            //Check which type of slicer to spawn
            if (slicerType.equals("slicer")) {
                RegularSlicer slicer = new RegularSlicer(polyline, spawnedSlicers + 1, waveNumber);
                slicers.add(slicer);
                Point point = slicer.getNext();
                slicerLocation.add(point);
            } else if (slicerType.equals("superslicer")) {
                SuperSlicer newSlicer = new SuperSlicer(polyline, spawnedSlicers + 1, waveNumber);
                slicers.add(newSlicer);
                Point point = newSlicer.getNext();
                slicerLocation.add(point);
            } else if (slicerType.equals("megaslicer")) {
                MegaSlicer slicer = new MegaSlicer(polyline, spawnedSlicers + 1, waveNumber);
                slicers.add(slicer);
                Point point = slicer.getNext();
                slicerLocation.add(point);
            } else if (slicerType.equals("apexslicer")){
                ApexSlicer slicer = new ApexSlicer(polyline, spawnedSlicers + 1, waveNumber);
                slicers.add(slicer);
                Point point = slicer.getNext();
                slicerLocation.add(point);
            }
            spawnedSlicers += 1;
            // Reset frame counter
            frameCount = 0;
        }

        //Get next wave from waveEvents after the time for the delay has passed
        if (eventType.equals("delay") && frameCount / FPS >= delayTime) {
            getNextWave = true;
        }

        if (spawnedSlicers == numberOfSlicers && !(waveEvents.isEmpty())) {
            getNextWave = true;
        }

        if (waveEvents.isEmpty() && slicers.isEmpty() && level < LEVELS) {
            level++;
            getWaveEvents();
            currentWaveNumber = 0;
            money = 500;
            lives = 25;
            List<String> a = new ArrayList<>();
            towerTypes = a;
            List<Sprite> b = new ArrayList<>();
            towers = b;
            bp.setLists(towerTypes, towers);
            timescale = 1;
            status = "Awaiting Start";
            waveStarted = false;
        }
        if (level == LEVELS && slicers.isEmpty()) {
            status = "Winner!";
//            Window.close();
        }

        // Update all sprites, and remove them if they've finished. Logic
        // taken from given working implementation of Project 1.
        for (int i = slicers.size() - 1; i >= 0; i--) {
            Sprite s = slicers.get(i);
            s.update(input);
            //Removes slicer, but first downcasts it depending on what kind of slicer (Sprite) it is
            if (s.getType().equals("slicer")) {
                RegularSlicer n = (RegularSlicer) s;
                Point point = n.getNext();
                slicerLocation.set(i, point);
                if (n.isFinished()) {
                    lives -= n.getPenalty();
                    slicers.remove(i);
                    slicerLocation.remove(i);
                }
                if (getReward == true) {
                    if (n.getWaveNumber() == currentWaveNumber) {
                        getReward = false;
                    }
                }
                if (n.isEliminated()) {
                    money += n.getReward();
                    slicers.remove(i);
                    slicerLocation.remove(i);

                }
            } else if (s.getType().equals("superslicer")) {
                SuperSlicer n = (SuperSlicer) s;
                Point point = n.getNext();
                slicerLocation.set(0, point);
                if (n.isFinished()) {
                    lives -= calcPenalty(n.getSpawnNumber(), numberOfSlicers, n.getPenalty());
                    slicers.remove(i);
                    slicerLocation.remove(i);
                }
                if (getReward == true) {
                    if (n.getWaveNumber() == currentWaveNumber) {
                        getReward = false;
                    }
                }
                if (n.isEliminated()) {
                    money += n.getReward();
                    slicers.remove(i);
                    slicerLocation.remove(i);
                    RegularSlicer s1 = new RegularSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    RegularSlicer s2 = new RegularSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    slicers.add(s1);
                    slicers.add(s2);

                }
            } else if (s.getType().equals("megaslicer")) {
                MegaSlicer n = (MegaSlicer) s;
                Point point = n.getNext();
                slicerLocation.set(0, point);
                if (n.isFinished()) {
                    lives -= calcPenalty(n.getSpawnNumber(), numberOfSlicers, n.getPenalty());
                    slicers.remove(i);
                    slicerLocation.remove(i);

                }
                if (getReward == true) {
                    if (n.getWaveNumber() == currentWaveNumber) {
                        getReward = false;
                    }
                }
                if (n.isEliminated()) {
                    money += n.getReward();
                    slicers.remove(i);
                    slicerLocation.remove(i);

                    SuperSlicer s1 = new SuperSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    SuperSlicer s2 = new SuperSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    slicers.add(s1);
                    slicers.add(s2);
                }
            } else if (s.getType().equals("apexslicer")){
                ApexSlicer n = (ApexSlicer) s;
                Point point = n.getNext();
                slicerLocation.set(0, point);
                if (n.isFinished()) {
                    lives -= calcPenalty(n.getSpawnNumber(), numberOfSlicers, n.getPenalty());
                    slicers.remove(i);
                    slicerLocation.remove(i);

                }
                if (getReward == true) {
                    if (n.getWaveNumber() == currentWaveNumber) {
                        getReward = false;
                    }
                }
                if (n.isEliminated()) {
                    money += n.getReward();
                    slicers.remove(i);
                    slicerLocation.remove(i);

                    MegaSlicer s1 = new MegaSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    MegaSlicer s2 = new MegaSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    MegaSlicer s3 = new MegaSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    MegaSlicer s4 = new MegaSlicer(n.getNewPolyline(), n.getSpawnNumber(), n.getWaveNumber());
                    slicers.add(s1);
                    slicers.add(s2);
                    slicers.add(s3);
                    slicers.add(s4);

                }
            }
        }

//        if (!slicers.isEmpty()) {
//            for (int i = 0; i < slicers.size(); i++)  {
//                Sprite s = slicers.get(i);
//                if (s.getType().equals("slicer")) {
//                    RegularSlicer n = (RegularSlicer) s;
//                    Point point = polyline.get(n.getTargetPointIndex());
//                    slicerLocation.set(i, point);
//                } else if (s.getType().equals("superslicer")) {
//                    SuperSlicer n = (SuperSlicer) s;
//                    Point point = polyline.get(n.getTargetPointIndex());
//                    slicerLocation.set(i, point);
//                } else if (s.getType().equals("megaslicer")) {
//                    MegaSlicer n = (MegaSlicer) s;
//                    Point point = polyline.get(n.getTargetPointIndex());
//                    slicerLocation.set(i, point);
//                } else if (s.getType().equals("apexslicer")) {
//                    ApexSlicer n = (ApexSlicer) s;
//                    Point point = polyline.get(n.getTargetPointIndex());
//                    slicerLocation.set(i, point);
//                }
//            }
//        }
//        if (!towerLocations.isEmpty()) {
//            System.out.println(towerLocations.get(towerLocations.size() - 1));
//
//        }
        targetPoints.clear();
        projectiles.clear();
        for (int i = 0; i < towers.size(); i++) {
            Point point = new Point(0,0);
            targetPoints.add(point);
            projectiles.add(new Projectile(point, towers.get(i).getCenter()));
        }

        if (!slicerLocation.isEmpty() && !bp.getArray().isEmpty()) {
            outcrop:
            for (int i = 0; i < towers.size(); i++) {
                Sprite s = towers.get(i);
                if (s.getType().equals("tank")) {
                    Tank n = (Tank) s;
                    for (int j = 0; j < slicerLocation.size(); j++) {
                        double toCheck = Math.pow(slicerLocation.get(j).x - towers.get(i).getCenter().x, 2) +
                                Math.pow(slicerLocation.get(j).y - towers.get(i).getCenter().y, 2);
                        if (toCheck < 10000) {
                            if (n.isReadyForNextTarget()) {
                                targetPoints.set(i, slicerLocation.get(j));
                                n.setTarget(targetPoints.get(i));
//                                System.out.println(targetPoints.get(i));
                            }
                        }
                    }
                } else if (s.getType().equals("supertank")) {
                    SuperTank n = (SuperTank) s;
                    for (int j = 0; j < slicerLocation.size(); j++) {
                        double toCheck = Math.pow(slicerLocation.get(j).x - towers.get(i).getCenter().x, 2) +
                                Math.pow(slicerLocation.get(j).y - towers.get(i).getCenter().y, 2);
                        if (toCheck < 22500) {
                            System.out.println("supertanktarget");
                        }
                    }

                }
            }

        }
        for (int i = 0; i < projectiles.size(); i++) {
            if (towers.get(i).getType().equals("tank")) {
                Tank s = (Tank) towers.get(i);
                if (!projectiles.get(i).getIsReady()) {
                    projectiles.get(i).increaseCount();
                }
                if (!projectiles.get(i).getIsReady() && projectiles.get(i).getProjectileCount() / FPS > (s.getProjectileCool()) / getTimescale()) {
                    projectiles.get(i).setIsReady(true);
                }
            }
        }
        for (int i = 0; i < targetPoints.size(); i++) {
            if (towers.get(i).getType().equals("tank")) {
                Tank s = (Tank) towers.get(i);
                s.setTarget(targetPoints.get(i));
                if (projectiles.get(i).getIsReady()) {
                    projectiles.get(i).setTarget(targetPoints.get(i));
                    projectiles.get(i).update(input);
                    s.setGetNextTarget(true);
                }
                s.update(input);
            }
        }






        // Close game if all lives are lost
//        if (lives <= 0) {
//            Window.close();
//        }
    }
}

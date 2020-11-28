package se.nocroft.model;

import se.nocroft.model.cars.Car;
import se.nocroft.model.cars.CarSetup;
import se.nocroft.model.cars.DrawableCar;
import se.nocroft.model.cars.FragileCar;
import se.nocroft.model.drivers.Driver;
import se.nocroft.util.CfgParser;
import se.nocroft.util.DirectionalRect;
import se.nocroft.util.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pontus on 2016-03-04.
 */
public class World implements Racetrack {
    public static final int WORLD_WIDTH = 1600;
    public static final int WORLD_HEIGHT = 1200;

    private final int INIT_DISTANCE_TO_GOAL = 100;
    private final int INIT_DISTANCE_TO_GOAL_SIDE = 25;

    private String track;
    private BufferedImage[] images;

    private FragileCar[] cars;
    // private GameObject[] controllers;
    private CarSetup[] setups;
    private List<GameObject> objects = new ArrayList<>();
    private DrawableCar[] drawables;
    private boolean[] passedBack;
    private boolean[] passedFront;

    private DirectionalRect goal;

    private int laps;

    private long startTime;
    private long time;
    private long countdown;
    private double deltaTime;
    private boolean finished = false;
    private double friction;

    public World() {
        loadData();
        initImages();
    }

    public void start(CarSetup[] setups) {
        this.setups = setups;
        createWorld(setups);
    }

    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
        time = getTime();
        updateCars(deltaTime);
        releaseCars();
        checkCollisions();
        updateObjects(deltaTime);
        checkLaps();
    }

    public FragileCar[] getCars() {
        return cars;
    }

    @Override
    public DrawableCar[] getDrawableCars() {
        return drawables;
    }

    @Override
    public List<GameObject> getObjects() {
        return objects;
    }

    @Override
    public boolean getFinished() {
        return finished;
    }

    public FragileCar[] getPlayers() {
        List<FragileCar> players = new ArrayList<>();
        for (int i = 0; i < setups.length; i++) {
            if (setups[i].driver == null) {
                players.add(cars[i]);
            }
        }
        FragileCar[] playerArr = new FragileCar[players.size()];
        return players.toArray(playerArr);
    }

    @Override
    public BufferedImage getBackground() {
        return images[0];
    }

    @Override
    public BufferedImage getForeground() {
        return images[1];
    }

    @Override
    public long getTime() {
        if (!finished) {
            return System.currentTimeMillis() - startTime - countdown;
        }
        return time;
    }

    private double timePassed = 0;
    private int fps = 0;

    @Override
    public int getFPS() {
        final double fpsUpdateInterval = 0.5;

        timePassed += deltaTime;
        if (timePassed > fpsUpdateInterval) {
            fps = (int) (1 / deltaTime);
            timePassed = 0;
        }
        return fps;
    }

    @Override
    public int getMaxLaps() {
        return laps;
    }

    private void createWorld(CarSetup[] setups) {
        findGoalLine();
        createCars(setups);
        initBotDrivers(setups);
        createMiscGameObjects();
        startTime = System.currentTimeMillis();
        System.out.println("World created with " + getPlayers().length + " players and " + (cars.length - getPlayers().length) + " bot(s).");
    }

    private void loadData() {
        CfgParser cfg = new CfgParser(CfgParser.STD_PATH);

        track = cfg.readString("trackName");
        laps = cfg.readInt("laps");
        countdown = cfg.readLong("countdown");
        friction = cfg.readDouble("friction");
    }

    private void findGoalLine() {
        /*
         * Do like a spawn-area with 4 pixels with certain colors for back and front.
         * Spawn cars in the area/square to start. New lap by coming from behind, going
         * through and leaving.
         */

        // Search for the marking pixels that are RED.
        int goalX = 0;
        int goalY = 0;
        for (int x = 0; x < images[0].getWidth(); x++) {
            for (int y = 0; y < images[0].getHeight(); y++) {
                // System.out.println(new Color(images[0].getRGB(x,y)) + "(" + x + "," + y +
                // ")");
                if (images[0].getRGB(x, y) == Color.RED.getRGB() && goalX == 0 && goalY == 0) {
                    goalX = x;
                    goalY = y;
                } else if (images[0].getRGB(x, y) == Color.RED.getRGB()) {

                    // Create the rectangle with correct alignment.
                    goal = new DirectionalRect(goalX, goalY, Math.max(Math.abs(goalX - x), Math.abs(goalY - y)), 400,
                            Math.abs(goalX - x) > Math.abs(goalY - y) ? DirectionalRect.Direction.UP
                                    : DirectionalRect.Direction.LEFT);
                }
            }
        }

        System.out.println("Goal created: ");
        System.out.println(goal);
    }

    private void initImages() {
        images = new BufferedImage[2];

        images[0] = ImageHandler.loadImage(track + "_bg");
        images[1] = ImageHandler.loadImage(track + "_fg");
    }

    private void createCars(CarSetup[] setups) {
        cars = new FragileCar[setups.length];
        drawables = new DrawableCar[cars.length];

        for (int i = 0; i < setups.length; i++) {
            if (goal.getDir() == DirectionalRect.Direction.LEFT) {
                cars[i] = spawnCarLeft(i, setups.length, setups[i]);
            } else {
                cars[i] = spawnCarUp(i, setups.length, setups[i]);
            }

            drawables[i] = (Car) cars[i];
        }

        passedBack = new boolean[cars.length];
        passedFront = new boolean[cars.length];
    }

    private void createMiscGameObjects() {
        for (int i = 0; i < cars.length; i++) {
            for (int j = 0; j < drawables[0].getGameObjects().length; j++) {
                objects.add(drawables[i].getGameObjects()[j]);
            }
        }
    }

    private void initBotDrivers(CarSetup[] setups) {
        // Create car controllers for bots
        for (int i = 0; i < setups.length; i++) {
            // My ugly hack for using custom bot-classes for other programmers to implement
            if (setups[i].driver != null) {
                try {
                    Driver bot = setups[i].driver.getDeclaredConstructor(FragileCar.class, String.class).newInstance(cars[i], track);

                    objects.add(bot);
                } catch (Exception e) {
                    System.out.println("A lot of things went wrong when loading your custom bot-class. Sorry... :/"
                            + "Your code probably has a lot of bugs.");
                    e.printStackTrace();
                }
            }
        }
    }

    private Car spawnCarLeft(int carIndex, int totalCars, CarSetup setup) {
        int goalHeight = goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).y -
                goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).y - INIT_DISTANCE_TO_GOAL_SIDE * 2;

        double distBetweenCars = goalHeight / ((double) totalCars);
        int y = goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).y +
                (int) (distBetweenCars * carIndex) + INIT_DISTANCE_TO_GOAL_SIDE;

        return new Car(
                setup.type,
                goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).x + INIT_DISTANCE_TO_GOAL,
                y,
                Math.PI * 3 / 2,
                friction
        );
    }

    private Car spawnCarUp(int carIndex, int totalCars, CarSetup setup) {
        int goalWidth = goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).x -
                goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).x - INIT_DISTANCE_TO_GOAL_SIDE * 2 - 50;

        double distBetweenCars = goalWidth / ((double) totalCars);
        int x = goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).x +
                (int) (distBetweenCars * carIndex) + INIT_DISTANCE_TO_GOAL_SIDE;

        return new Car(
                setup.type,
                x,
                goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).y + INIT_DISTANCE_TO_GOAL,
                0,
                friction
        );
    }

    private void checkCollisions() {
        for (FragileCar car : getCars()) {
            new Thread(() -> { // CONCURRENCY FOR EFFICIENCY YES
                for (int x = 0; x < car.getWidth(); x++) {
                    for (int y = 0; y < car.getHeight(); y++) {
                        try {
                            if (car.getImgs()[car.getFrame()].getRGB(x, y) != 0) {
                                if (images[1].getRGB(car.getX() + x, car.getY() + y) != 0) {
                                    car.reset();
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            car.reset();
                        }

                    }
                }
            }).start();
        }
    }

    private int places = 0;

    private void checkLaps() {
        boolean allFinished = true;
        for (int i = 0; i < cars.length; i++) {
            if (goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.FRONT && passedBack[i]) {
                cars[i].newLap();
                // Finished?
                if (cars[i].getLaps() == laps) {
                    places++;
                    cars[i].finish(getTime(), places);
                    cars[i].turnOff(true);
                } else {
                    System.out.println(cars[i]);
                }
            } else if (goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.BACK
                    && passedFront[i]) {
                cars[i].reset();
            }

            if (cars[i].getLaps() != laps) {
                allFinished = false;
            }

            passedBack[i] = (goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.BACK);
            passedFront[i] = (goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.FRONT);
        }
        finished = allFinished;
    }

    private boolean released = false;

    private void releaseCars() {
        if (!released && time > 0) {
            for (FragileCar car : cars) {
                car.setLocked(false);
            }
            released = true;
        }
    }

    private void updateCars(double dTime) {
        for (FragileCar c : cars) {
            c.update(dTime);
        }
    }

    private void updateObjects(double dTime) {
        for (GameObject obj : objects) {
            if (obj != null) {
                obj.update(dTime);
            }
        }
    }

    @Override
    public String toString() {
        return "World with cars hehehe.";
    }
}

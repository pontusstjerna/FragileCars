package se.nocroft.model.carcontrollers;

import se.nocroft.model.GameObject;
import se.nocroft.model.carcontrollers.util.BotPoint;
import se.nocroft.model.cars.Car;
import se.nocroft.model.cars.FragileCar;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pontu on 2016-04-08.
 */
public class CheckBot implements GameObject {

    private FragileCar car;
    private List<BotPoint> checkPoints = new ArrayList<>();
    private Queue<BotPoint> crashes = new ArrayDeque<>();
    private Random rand;
    private Point spawnPoint;

    private enum Dir {
        STRAIGHT, LEFT, RIGHT
    }

    private final int STICK_LENGTH = 100;

    private double distance = 0;
    private int cpIndex = 0;
    private int lastX, lastY;

    public CheckBot(FragileCar car, String trackName) {
        this.car = car;
        rand = new Random();
        spawnPoint = new Point(car.getX(), car.getY());
        lastX = spawnPoint.x;
        lastY = spawnPoint.y;

        // addStdPoints(Integer.valueOf(trackName));
    }

    @Override
    public void update(double deltaTime) {
        drive(deltaTime);
        checkReset();
        // Distance is the distance between the last pos and current pos
        distance += Point.distance(car.getX(), car.getY(), lastX, lastY);
        lastX = car.getX();
        lastY = car.getY();
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        if (car.getName().equals("BLUE car")) {
            g.setColor(Color.BLUE);
        } else if (car.getName().equals("GREEN car")) {
            g.setColor(Color.GREEN);
        } else if (car.getName().equals("RED car")) {
            g.setColor(Color.RED);
        } else if (car.getName().equals("YELLOW car")) {
            g.setColor(Color.YELLOW);
        }

        // Paint balls
        for (BotPoint p : getBotPoints()) {
            int s = (int) (scale * 10);
            g.fillRoundRect((int) (p.x * scale) - (s / 2) + scaleX, (int) (p.y * scale) - (s / 2), s, s, s, s);
            int dist = (int) (scale * p.getRadius() * 2);
            g.drawRoundRect((int) (p.x * scale) - (dist / 2) + scaleX, (int) (p.y * scale) - (dist / 2), dist, dist,
                    dist, dist);
        }

        // Paint sticks
        int stickX = (int) (getStickX() * scale) + scaleX;
        int stickY = (int) (getStickY() * scale);

        g.drawLine((int) (car.getX() * scale) + scaleX, (int) (car.getY() * scale), stickX, stickY);
    }

    private List<BotPoint> getBotPoints() {
        List<BotPoint> all = new ArrayList<>(checkPoints.size() + crashes.size());
        all.addAll(checkPoints);
        all.addAll(crashes);
        return all;
    }

    private int getStickX() {
        return (int) stickX();
    }

    private int getStickY() {
        return (int) stickY();
    }

    // Follow the previous best path
    private void drive(double dTime) {
        if (cpIndex == checkPoints.size()) { // If passed all checkpoints
            car.accelerate();
            BotPoint closestCrashInFront = closestInFront(car, crashes);
            if (closestCrashInFront == null) {
                search(dTime);
            } else {
                turn(turnFromPoint(car, closestCrashInFront), dTime);
            }

            final int MIN_SPAWN_DISTANCE = 50;

            // Add checkpoints only if distance to spawn is enough and speed is enough
            if (car.getAcceleration() > Car.speedLimit / 2
                    && Point.distance(car.getX(), car.getY(), spawnPoint.x, spawnPoint.y) > MIN_SPAWN_DISTANCE) {
                addCheckPoints();
            }
        } else { // Follow the points
            BotPoint currentCP = checkPoints.get(cpIndex);

            if (onPoint(currentCP)) {
                cpIndex++;
            } else {
                car.accelerate();

                turn(turnToPoint(car, currentCP), dTime);
            }
            /*
             * if(isInFront(car, currentCP)){
             * 
             * }else{ cpIndex++; System.out.println("Check at point " + cpIndex); }
             */
        }
    }

    private boolean onPoint(BotPoint point) {
        return point.distance(car.getMiddleX(car.getX()), car.getMiddleY(car.getY())) < point.getRadius();
    }

    private Dir turnToPoint(FragileCar car, BotPoint p) {
        if (getBearing(car, p) > 0) {
            return Dir.RIGHT;
        } else if (getBearing(car, p) < 0) {
            return Dir.LEFT;
        }

        return Dir.STRAIGHT;
    }

    private Dir turnFromPoint(FragileCar car, BotPoint p) {
        if (getBearing(car, p) >= 0) {
            return Dir.LEFT;
        } else if (getBearing(car, p) < 0) {
            return Dir.RIGHT;
        }

        return Dir.STRAIGHT;
    }

    // Tries to find a valid way
    private Dir dir = Dir.STRAIGHT;
    private int turnTimer = 0;
    private int turnTime = 0;

    private void search(double dTime) {

        // Minimum time in a turn (frames)
        final int minTimeTurn = (int) (1000 * dTime);

        // Maximum time in a turn
        final int maxTimeTurn = (int) (10000 * dTime);

        if (turnTimer >= turnTime) { // Change direction
            turnTimer = 0;
            turnTime = rand.nextInt(maxTimeTurn) + minTimeTurn;
            dir = Dir.values()[rand.nextInt(3)];
        } else {
            turn(dir, dTime);
            turnTimer++;
        }
    }

    // If searching, regularly add new checkpoints as long as not dying
    private void addCheckPoints() {
        final int spawn_freq = 10;

        if ((int) distance > spawn_freq) {
            checkPoints.add(new BotPoint(getStickX(), getStickY()));
            cpIndex++;
            distance = 0;
        }
    }

    // Is the point in front of the stick?
    private boolean isInFront(FragileCar car, Point point) {
        double bearing = getBearing(car, point);
        return bearing < Math.PI / 2 && bearing > -Math.PI / 2;
    }

    private void checkReset() {
        if (spawnPoint.distance(car.getX(), car.getY()) < 1 && distance > 10) {
            reset();
        }
    }

    private void keepSpeed(double speed) {
        if (car.getAcceleration() < speed) {
            car.accelerate();
        } else if (car.getAcceleration() > speed) {
            car.brake();
        }
    }

    private int deathCounter = 0;

    private void reset() {
        addCrash();

        System.out.println("Index: " + cpIndex);
        if (cpIndex < checkPoints.size() - 1 || cpIndex == 0) {
            removeCheckPoints(1);
            System.out.println("Didn't reach.");
        } else if (checkPoints.size() > 0) {
            // Remove last checkpoint inside ANY of the crash-markers
            boolean remove = false;
            for (BotPoint crash : crashes) {
                // Remove last checkpoint if inside crash-radius
                if (crash.distance(checkPoints.get(checkPoints.size() - 1)) <= crashRadius) {
                    remove = true;
                }
            }

            // If in any way got stuck
            final int DEATH_LIMIT = 2;
            if (remove) {
                removeCheckPoints(1);
            } else if (deathCounter >= DEATH_LIMIT) {
                removeCheckPoints(1);
                deathCounter = 0;
            } else {
                // Died without removing anything yet
                deathCounter++;
            }
        }
        System.out.println("nCheckPoints: " + checkPoints.size());
        distance = 0;
        cpIndex = 0;
    }

    private final int crashRadius = 40;

    private void addCrash() {
        final int maxCrashes = 3;

        // Add new crash to queue
        crashes.add(new BotPoint(lastX, lastY, crashRadius));
        if (crashes.size() > maxCrashes) {
            crashes.remove();
        }
    }

    private void removeCheckPoints(int nCheckPoints) {
        int CPsize = checkPoints.size() - 1;
        for (int i = CPsize; i >= 0 && i > CPsize - nCheckPoints; i--) {
            checkPoints.remove(i);
        }
    }

    private BotPoint closestInFront(FragileCar car, Iterable<BotPoint> points) {

        if (points.iterator().hasNext()) {
            BotPoint closestInFront = points.iterator().next();

            for (BotPoint p : points) {
                if (isInFront(car, p)
                        && p.distance(car.getX(), car.getY()) < closestInFront.distance(car.getX(), car.getY())) {
                    closestInFront = p;
                }
            }

            return closestInFront;
        } else {
            return null;
        }
    }

    private void turn(Dir dir, double dTime) {
        switch (dir) {
            case LEFT:
                car.turnLeft(dTime);
                break;
            case STRAIGHT:
                break;
            case RIGHT:
                car.turnRight(dTime);
                break;
        }
    }

    private double stickX() {
        return car.getX() + Math.sin(car.getHeading()) * STICK_LENGTH;
    }

    private double stickY() {
        return car.getY() - Math.cos(car.getHeading()) * STICK_LENGTH;
    }

    private double getBearing(FragileCar car, Point point) {
        return getPI(getHeadingToPoint(point, car.getX(), car.getY()) - Math.PI / 2 - getPI(car.getHeading()));

        /*
         * (atan2(dy,dx) - pi/2))180 - carhead180
         */
    }

    private double getHeadingToPoint(Point p, double x, double y) {
        return Math.atan2(y - p.y, x - p.x);
    }

    private double getPI(double angle) {
        angle = angle % (Math.PI * 2);
        if (angle >= Math.PI && angle > 0) {
            return angle - Math.PI * 2;
        } else if (angle <= -Math.PI) {
            return angle + Math.PI * 2;
        }
        return angle;
    }

    private void addStdPoints(int track) {
        switch (track) {
            case 1:
                checkPoints.add(new BotPoint(100, 1050));
                checkPoints.add(new BotPoint(100, 800));
                checkPoints.add(new BotPoint(140, 400));
                checkPoints.add(new BotPoint(140, 200));
                checkPoints.add(new BotPoint(200, 100));
                checkPoints.add(new BotPoint(800, 100));
                checkPoints.add(new BotPoint(1300, 100));
                checkPoints.add(new BotPoint(1400, 200));
                checkPoints.add(new BotPoint(1400, 800));
                checkPoints.add(new BotPoint(1350, 1050));
                checkPoints.add(new BotPoint(1200, 1100));
                checkPoints.add(new BotPoint(1000, 1000));
                checkPoints.add(new BotPoint(1000, 400));
                checkPoints.add(new BotPoint(900, 300));
                checkPoints.add(new BotPoint(500, 400));
                checkPoints.add(new BotPoint(500, 600));
                checkPoints.add(new BotPoint(700, 700));
                checkPoints.add(new BotPoint(700, 800));
                checkPoints.add(new BotPoint(600, 900));
                checkPoints.add(new BotPoint(100, 1000));
                checkPoints.add(new BotPoint(500, 600));
                checkPoints.add(new BotPoint(700, 700));
                checkPoints.add(new BotPoint(700, 800));
                checkPoints.add(new BotPoint(600, 900));
                checkPoints.add(new BotPoint(100, 1000));
                break;
            default:
                break;
        }
    }
}

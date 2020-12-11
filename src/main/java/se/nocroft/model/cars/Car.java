package se.nocroft.model.cars;

import se.nocroft.model.GameObject;
import se.nocroft.model.misc.SkidmarkCreator;
import se.nocroft.model.misc.SmokeController;
import se.nocroft.util.CfgParser;
import se.nocroft.util.Geom;
import se.nocroft.util.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class Car implements DrawableCar, FragileCar {
    public enum Cars {
        GREEN, YELLOW, RED, BLUE
    }

    public enum States {
        STRAIGHT, LEFT, RIGHT
    }

    private States state = States.STRAIGHT;

    private double x, y;
    private double hypotenusa;
    private final int originX, originY;
    private double heading;
    private double physHeading;
    private final double originHeading;
    private boolean accelerating = false;
    private double acceleration;

    private int laps = 0;
    private boolean locked = true;
    private boolean turnedOff = false;
    private long finished = 0;
    private int place = 0;

    private double friction;

    public static final int speedLimit = 500;
    private final int reverseLimit = -50;

    private final Point[] wheels;

    private Cars kind;
    private BufferedImage[] images;

    private SmokeController smoke;
    private SkidmarkCreator skids;

    public Car(Cars kind, int x, int y, double heading, double friction) {
        this.kind = kind;
        originX = x;
        originY = y;
        originHeading = heading;
        this.friction = friction;

        reset();
        setImages();

        wheels = new Point[] { new Point(10, 10), new Point(getWidth() - 10, 10), new Point(10, getHeight() - 10),
                new Point(getWidth() - 10, getHeight() - 10) };

        hypotenusa = Math.sqrt(getWidth() * getWidth() / 4 + getHeight() * getHeight() / 4);

        smoke = new SmokeController(this);
        skids = new SkidmarkCreator(20);
    }

    @Override
    public void update(double deltaTime) {
        if (!locked) {
            physHeading = drift(friction, acceleration / speedLimit, heading, physHeading);

            x = (x + acceleration * Math.sin(physHeading) * deltaTime);
            y = (y - acceleration * Math.cos(physHeading) * deltaTime);

            if (!accelerating) {
                engineBrake();
            } else {
                accelerating = false;
            }

            if (Math.abs(Geom.getPI(physHeading) - Geom.getPI(heading)) > skids.getThreshold()) {
                for (int i = 0; i < wheels.length; i++) {
                    skids.drift(getRelX(wheels[i].x, wheels[i].y), getRelY(wheels[i].x, wheels[i].y), heading);
                }
            }
        }
    }

    @Override
    public void reset() {
        x = originX;
        y = originY;
        heading = originHeading;
        physHeading = originHeading;

        acceleration = 0;
    }

    @Override
    public void newLap() {
        laps++;
    }

    @Override
    public void finish(long time, int place) {
        finished = time;
        this.place = place;
        System.out.println(this + " finished at place " + place + " with time " + time / 1000 + ":" + time % 1000);
    }

    @Override
    public long getFinished() {
        return finished;
    }

    @Override
    public void setLocked(boolean lock) {
        locked = lock;
    }

    @Override
    public void turnOff(boolean turnOff) {
        turnedOff = turnOff;
    }

    @Override
    public int getLaps() {
        return laps;
    }

    @Override
    public int getPlace() {
        return place;
    }

    @Override
    public int getX() {
        return (int) x;
    }

    @Override
    public int getY() {
        return (int) y;
    }

    @Override
    public int getRelX(double x, double y) {
        return (int) (getRotX() + x * Math.cos(-heading) + y * Math.sin(-heading));
    }

    @Override
    public int getRelY(double x, double y) {
        return (int) (getRotY() + y * Math.cos(-heading) + x * Math.sin(heading));
    }

    private int getRotX() {
        return (int) (getMiddleX(this.x) + hypotenusa * Math.sin(heading - Math.PI / 4));
    }

    private int getRotY() {
        return (int) (getMiddleY(this.y) - hypotenusa * Math.cos(heading - Math.PI / 4));
    }

    @Override
    public double getMiddleX(double x) {
        return x + ((getWidth()) * Math.cos(heading) * Math.cos(heading)
                + (getHeight()) * Math.sin(heading) * Math.sin(heading)) / 2;
    }

    @Override
    public double getMiddleY(double y) {
        return y + ((getWidth()) * Math.sin(heading) * Math.sin(heading)
                + (getHeight()) * Math.cos(heading) * Math.cos(heading)) / 2;
    }

    @Override
    public double getMiddleX() {
        return getMiddleX(x);
    }

    @Override
    public double getMiddleY() {
        return getMiddleY(y);
    }

    @Override
    public Point[] getWheels() {
        return wheels;
    }

    @Override
    public int getWidth() {
        return images[0].getWidth();
    }

    @Override
    public int getHeight() {
        return images[0].getHeight();
    }

    @Override
    public double getHeading() {
        return Geom.getPI(heading);
    }

    @Override
    public double getAcceleration() {
        return acceleration;
    }

    @Override
    public void accelerate() {
        if (!turnedOff && !locked) {
            accelerating = true;

            if (acceleration < speedLimit) {
                acceleration += speedLimit / 200;
            } else {
                acceleration = speedLimit;
            }
        }
    }

    @Override
    public void engineBrake() {
        accelerating = false;

        if (acceleration > 0) {
            acceleration -= 0.2 * (speedLimit / 100);
        } else {
            acceleration = 0;
        }
    }

    @Override
    public void brake() {
        accelerating = true;

        if (acceleration > reverseLimit) {
            acceleration -= speedLimit / 50;
        } else {
            acceleration = reverseLimit;
        }
    }

    @Override
    public void turnRight(double deltaTime) {
        if ((acceleration > 10 || acceleration < -10) && !locked) {
            heading = (heading + deltaTime * 4) % (Math.PI * 2);
        }
        state = States.RIGHT;
    }

    @Override
    public void turnLeft(double deltaTime) {
        if ((acceleration > 10 || acceleration < -10) && !locked) {
            heading = (heading - deltaTime * 4) % (Math.PI * 2);
        }
        state = States.LEFT;
    }

    @Override
    public void release() {
        state = States.STRAIGHT;
    }

    @Override
    public BufferedImage[] getImgs() {
        return images;
    }

    @Override
    public int getFrame() {
        return state.ordinal();
    }

    @Override
    public GameObject[] getGameObjects() {

        // Hopefully a temporary fix, because this is pretty ugly
        CfgParser parser = new CfgParser(CfgParser.STD_PATH);
        boolean smokeEnabled = parser.readBoolean("smokeEnabled");
        boolean skidsEnabled = parser.readBoolean("skidmarksEnabled");
        parser = null;
        if (smokeEnabled && skidsEnabled) {
            return new GameObject[] { smoke, skids };
        } else if (smokeEnabled) {
            return new GameObject[] { smoke };
        } else if (skidsEnabled) {
            return new GameObject[] { skids };
        }

        return new GameObject[0];
    }

    @Override
    public String toString() {
        return kind.name() + " car at (" + getX() + "," + getY() + ") with " + laps + " laps";
    }

    @Override
    public String getName() {
        return kind.name() + " car";
    }

    private double drift(double driftFactor, double speedFrac, double carHeading, double physHeading) {
        // Rescale the driftFactor
        driftFactor = 1 - driftFactor / 100;

        // skillnad > 180 = snurra upp ett varv
        // skillnad < -180 = snurra ner ett varv

        double deltaAngle = carHeading - physHeading;
        if (deltaAngle < -Math.PI) {
            physHeading -= Math.PI * 2;
        } else if (deltaAngle > Math.PI) {
            physHeading += Math.PI * 2;
        }
        deltaAngle = carHeading - physHeading;

        double newAngle = (carHeading - deltaAngle * speedFrac * driftFactor) % (Math.PI * 2);
        if (newAngle > Math.PI) {
            newAngle = (carHeading - (deltaAngle * speedFrac * driftFactor + Math.PI * 2)) % (Math.PI * 2);
        } else if (newAngle < -Math.PI) {
            newAngle = (carHeading - (deltaAngle * speedFrac * driftFactor - Math.PI * 2)) % (Math.PI * 2);
        }

        return newAngle;
    }

    private void setImages() {
        BufferedImage image = ImageHandler.loadImage("car" + kind.name());
        images = new BufferedImage[States.values().length];

        for (int i = 0; i < States.values().length; i++) {
            images[i] = ImageHandler.cutImage(image, 0, i, image.getWidth() / States.values().length,
                    image.getHeight());
        }
    }
}

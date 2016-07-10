package model.cars;

import util.ImageHandler;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class Car implements DrawableCar, FragileCar {
    public enum Cars {GREEN, YELLOW, RED, BLUE}

    private double x,y;
    private final int originX, originY;
    private double heading;
    private double physHeading;
    private final double originHeading;
    private boolean accelerating = false;

    private int laps = 0;
    private boolean locked = true;
    private boolean turnedOff = false;
    private long finished = 0;
    private int place = 0;

    private double friction;

    public static final int speedLimit = 500;
    private final int reverseLimit = -50;

    private Cars kind;
    private BufferedImage image;

    private double acceleration;

    public Car(Cars kind, int x, int y, double heading, double friction){
        this.kind = kind;
        originX = x;
        originY = y;
        originHeading = heading;
        //TODO Fix friction in constructor
        this.friction = friction;

        reset();

        image = ImageHandler.loadImage("car" + kind.name());
    }

    @Override
    public void update(double deltaTime){
        if(!locked) {
            physHeading = drift(friction, acceleration/speedLimit, heading, physHeading);

            x = (x + acceleration * Math.sin(physHeading) * deltaTime);
            y = (y - acceleration * Math.cos(physHeading) * deltaTime);

            if (!accelerating) {
                engineBrake();
            } else {
                accelerating = false;
            }
        }
    }

    @Override
    public void reset(){
        x = originX;
        y = originY;
        heading = originHeading;
        physHeading = originHeading;

        acceleration = 0;
    }

    @Override
    public void newLap(){
        laps++;
    }

    @Override
    public void finish(long time, int place){
        finished = time;
        this.place = place;
        System.out.println(this + " finished at place " + place + " with time " + time/1000 + ":" + time % 1000);
    }

    @Override
    public long getFinished(){
        return finished;
    }

    @Override
    public void setLocked(boolean lock){
        locked = lock;
    }

    @Override
    public void turnOff(boolean turnOff){
        turnedOff = turnOff;
    }

    @Override
    public int getLaps(){
        return laps;
    }

    @Override
    public int getPlace(){
        return place;
    }

    @Override
    public int getX(){
        return (int)(x - image.getWidth()/2);
    }

    @Override
    public int getY(){
        return (int)(y - image.getHeight()/2);
    }

    @Override
    public double getHeading(){
        return heading;
    }

    @Override
    public double getAcceleration(){
        return acceleration;
    }

    @Override
    public void accelerate() {
        if(!turnedOff){
            accelerating = true;

            if(acceleration < speedLimit){
                acceleration += speedLimit/200;
            }else{
                acceleration = speedLimit;
            }
        }
    }

    @Override
    public void engineBrake(){
        accelerating = false;

        if(acceleration > 0){
            acceleration -= 0.2*(speedLimit/100);
        }else{
            acceleration = 0;
        }
    }

    @Override
    public void brake() {
        accelerating = true;

        if(acceleration > reverseLimit){
            acceleration -= speedLimit/50;
        }else{
            acceleration = reverseLimit;
        }
    }

    @Override
    public void turnRight(double deltaTime) {
        if((acceleration > 10 || acceleration < -10) && !locked){
            heading = (heading + deltaTime*4) % (Math.PI*2);
        }
    }

    @Override
    public void turnLeft(double deltaTime) {
        if((acceleration > 10 || acceleration < -10) && !locked){
            heading = (heading - deltaTime*4) % (Math.PI*2);
        }
    }

    @Override
    public BufferedImage getImg() {
        return image;
    }

    @Override
    public String toString(){
        return kind.name() + " car at (" + getX() + "," + getY() + ") with " + laps + " laps";
    }

    @Override
    public String getName(){
        return kind.name() + " car";
    }

    private double drift(double driftFactor, double speedFrac, double carHeading, double physHeading){
        //Rescale the driftFactor
        driftFactor = 1 - driftFactor/100;

        //skillnad > 180 = snurra upp ett varv
        //skillnad < -180 = snurra ner ett varv

        double deltaAngle = carHeading - physHeading;
        if(deltaAngle < -Math.PI){
            physHeading -= Math.PI*2;
        }else if(deltaAngle > Math.PI){
            physHeading += Math.PI*2;
        }
        deltaAngle = carHeading - physHeading;

        double newAngle = (carHeading - deltaAngle*speedFrac*driftFactor) % (Math.PI*2);
        if(newAngle > Math.PI){
            newAngle = (carHeading - (deltaAngle*speedFrac*driftFactor + Math.PI*2)) % (Math.PI*2);
        }else if(newAngle < -Math.PI){
            newAngle = (carHeading - (deltaAngle*speedFrac*driftFactor - Math.PI*2)) % (Math.PI*2);
        }

        return newAngle;
    }
}

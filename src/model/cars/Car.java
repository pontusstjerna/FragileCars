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
    private final double originHeading;
    private boolean accelerating = false;

    private int laps = 0;
    private boolean locked = true;
    private boolean turnedOff = false;
    private long finished = 0;
    private int place = 0;

    private final int speedLimit = 500;
    private final int reverseLimit = -50;

    private Cars kind;
    private BufferedImage image;

    private double acceleration;

    public Car(Cars kind, int x, int y, double heading){
        this.kind = kind;
        originX = x;
        originY = y;
        originHeading = heading;

        reset();

        image = ImageHandler.loadImage("car" + kind.name());
    }

    @Override
    public void update(double deltaTime){
        if(!locked) {
            x = (x + acceleration * Math.sin(heading) * deltaTime);
            y = (y - acceleration * Math.cos(heading) * deltaTime);

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
        if((acceleration > 1 || acceleration < -1) && !locked){
            heading = (heading + deltaTime*4) % (Math.PI*2);
        }
    }

    @Override
    public void turnLeft(double deltaTime) {
        if((acceleration > 1 || acceleration < -1) && !locked){
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
}

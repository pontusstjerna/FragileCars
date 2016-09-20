package model.cars;

import model.GameObject;
import model.misc.SmokeController;
import util.ImageHandler;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class Car implements DrawableCar, FragileCar {
    public enum Cars {GREEN, YELLOW, RED, BLUE}
    public enum States {STRAIGHT, LEFT, RIGHT}

    private States state = States.STRAIGHT;

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
    private BufferedImage[] images;

    private SmokeController smoke;

    private double acceleration;

    public Car(Cars kind, int x, int y, double heading, double friction){
        this.kind = kind;
        originX = x;
        originY = y;
        originHeading = heading;
        //TODO Fix friction in constructor
        this.friction = friction;

        reset();
        setImages();

        smoke = new SmokeController(this);
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
        return (int)(x - images[0].getWidth()/2);
    }

    @Override
    public int getY(){
        return (int)(y - images[0].getHeight()/2);
    }

    @Override
    public int getWidth(){
        return images[0].getWidth();
    }

    @Override
    public int getHeight(){
        return images[0].getHeight();
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
        state = States.RIGHT;
    }

    @Override
    public void turnLeft(double deltaTime) {
        if((acceleration > 10 || acceleration < -10) && !locked){
            heading = (heading - deltaTime*4) % (Math.PI*2);
        }
        state = States.LEFT;
    }

    @Override
    public void release(){
        state = States.STRAIGHT;
    }

    @Override
    public BufferedImage[] getImgs() {
        return images;
    }

    @Override
    public int getFrame(){
        return state.ordinal();
    }

    @Override
    public GameObject getSmoke(){ return smoke; }

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

    private void setImages(){
        BufferedImage image = ImageHandler.loadImage("car" + kind.name());
        images = new BufferedImage[States.values().length];

        for(int i = 0; i < States.values().length; i++){
            images[i] = ImageHandler.cutImage(image, 0, i, image.getWidth()/States.values().length, image.getHeight());
        }
    }
}

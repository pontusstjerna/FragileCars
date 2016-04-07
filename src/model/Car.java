package model;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class Car implements FragileCar {
    public enum Cars {BLUE, GREEN, RED, YELLOW}

    private double x,y;
    private final int originX, originY;
    private double heading;
    private final double originHeading;
    private boolean accelerating = false;

    private int laps = 0;

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
        x = (x + acceleration*Math.sin(heading)*deltaTime);
        y = (y - acceleration*Math.cos(heading)*deltaTime);

        if(!accelerating){
            engineBrake();
        }else{
            accelerating = false;
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

    }

    @Override
    public int getLaps(){
        return laps;
    }

    @Override
    public int getX(){
        return (int)x;
    }

    @Override
    public int getY(){
        return (int)y;
    }

    @Override
    public double getHeading(){
        return heading;
    }

    @Override
    public void accelerate() {
        accelerating = true;

        if(acceleration < speedLimit){
            acceleration += speedLimit/200;
        }else{
            acceleration = speedLimit;
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
        if(acceleration > 1 || acceleration < -1){
            heading = (heading + deltaTime*4) % (Math.PI*2);
        }
    }

    @Override
    public void turnLeft(double deltaTime) {
        if(acceleration > 1 || acceleration < -1){
            heading = (heading - deltaTime*4) % (Math.PI*2);
        }
    }

    @Override
    public BufferedImage getImg() {
        return image;
    }

    @Override
    public String toString(){
        return kind.name() + " car at (" + getX() + "," + getY() + ").";
    }
}

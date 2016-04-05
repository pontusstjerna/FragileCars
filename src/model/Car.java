package model;

import util.Vector2D;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class Car implements FragileCar {
    public enum Cars {BLUE, GREEN, RED, YELLOW}

    private double x,y;
    private double heading;
    private Vector2D vector;

    private Cars kind;
    private BufferedImage image;

    private double acceleration;

    public Car(Cars kind, int x, int y){
        this.kind = kind;
        this.x = x;
        this.y = y;

        image = ImageHandler.loadImage("car" + kind.name());

        vector = new Vector2D(0,1);
    }

    @Override
    public void update(double deltaTime){

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
    public Vector2D getVector(){
        return vector;
    }

    @Override
    public void accelerate() {

    }

    @Override
    public void brake() {

    }

    @Override
    public void turnRight() {

    }

    @Override
    public void turnLeft() {

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

package model;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class Car implements FragileCar {
    public enum Cars {BLUE, GREEN, RED, YELLOW}

    private Cars kind;

    public Car(Cars kind){
        this.kind = kind;
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
        return null;
    }
}

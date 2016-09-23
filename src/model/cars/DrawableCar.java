package model.cars;

import model.GameObject;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-07.
 */
public interface DrawableCar {
    int getX();
    int getY();
    int getRelX(double x);
    int getRelY(double y);
    int getWidth();
    int getHeight();
    double getHeading();

    long getFinished();
    int getLaps();
    int getPlace();

    String toString();
    String getName();

    BufferedImage[] getImgs();
    int getFrame();
    GameObject getSmoke();
}

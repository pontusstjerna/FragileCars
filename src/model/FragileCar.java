package model;

import util.Vector2D;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public interface FragileCar {
    int getX();
    int getY();
    double getHeading();
    double getAcceleration();

    void update(double deltaTime);
    void reset();
    void newLap();
    void finish(long time, int place);
    long getFinished();
    int getLaps();
    void setLocked(boolean lock);
    void turnOff(boolean turnOff);

    void accelerate();
    void engineBrake();
    void brake();
    void turnRight(double deltaTime);
    void turnLeft(double deltaTime);

    String toString();
    String getName();

    BufferedImage getImg();
}

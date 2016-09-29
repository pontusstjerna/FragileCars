package model.cars;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public interface FragileCar {
    int getX();
    int getY();
    int getRelX(double x, double y);
    int getRelY(double x, double y);
    Point[] getWheels();
    int getWidth();
    int getHeight();
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

    BufferedImage[] getImgs();
    int getFrame();

    void accelerate();
    void engineBrake();
    void brake();
    void turnRight(double deltaTime);
    void turnLeft(double deltaTime);
    void release();

    String toString();
    String getName();
}

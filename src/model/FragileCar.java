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

    void update(double deltaTime);

    void accelerate();
    void brake();
    void turnRight();
    void turnLeft();

    Vector2D getVector();

    String toString();

    BufferedImage getImg();
}

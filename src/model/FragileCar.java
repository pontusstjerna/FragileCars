package model;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public interface FragileCar {
    void accelerate();
    void brake();
    void turnRight();
    void turnLeft();

    String toString();

    BufferedImage getImg();
}

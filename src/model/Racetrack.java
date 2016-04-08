package model;

import model.cars.DrawableCar;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public interface Racetrack {
    long getTime();
    DrawableCar[] getDrawables();
    boolean getFinished();
    BufferedImage getBackground();
    BufferedImage getForeground();
}

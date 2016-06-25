package model;

import model.carcontrollers.CarController;
import model.carcontrollers.DrawableBot;
import model.cars.DrawableCar;

import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public interface Racetrack {
    long getTime();
    double getFPS();
    DrawableCar[] getDrawables();
    CarController[] getBots();
    boolean getFinished();
    BufferedImage getBackground();
    BufferedImage getForeground();
}

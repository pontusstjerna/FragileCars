package se.nocroft.model;

import model.cars.DrawableCar;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by pontu on 2016-04-05.
 */
public interface Racetrack {
    long getTime();

    int getFPS();

    DrawableCar[] getDrawableCars();

    ArrayList<GameObject> getObjects();

    boolean getFinished();

    int getMaxLaps();

    BufferedImage getBackground();

    BufferedImage getForeground();
}

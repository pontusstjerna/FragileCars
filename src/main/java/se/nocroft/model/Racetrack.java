package se.nocroft.model;

import se.nocroft.model.cars.DrawableCar;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pontu on 2016-04-05.
 */
public interface Racetrack {
    long getTime();

    int getFPS();

    DrawableCar[] getDrawableCars();

    List<GameObject> getObjects();

    boolean getFinished();

    int getMaxLaps();

    BufferedImage getBackground();

    BufferedImage getForeground();
}

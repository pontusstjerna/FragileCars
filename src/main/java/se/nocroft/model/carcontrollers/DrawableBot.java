package se.nocroft.model.carcontrollers;

import se.nocroft.model.carcontrollers.util.BotPoint;
import se.nocroft.model.cars.FragileCar;

import java.util.List;

/**
 * Created by Pontus on 2016-04-13.
 */
public interface DrawableBot {
    List<BotPoint> getBotPoints();

    int getWallThreshold();

    int getStickLength();

    int getStickX();

    int getStickY();

    FragileCar getCar();
}

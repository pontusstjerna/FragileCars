package model.carcontrollers;

import model.carcontrollers.util.WallPoint;
import model.cars.FragileCar;

import java.util.List;

/**
 * Created by Pontus on 2016-04-13.
 */
public interface DrawableBot {
    List<WallPoint> getWallPoints();
    int getWallThreshold();
    int getStickLength();
    int getStickX();
    int getStickY();

    FragileCar getCar();
}
package model.carcontrollers;

import model.cars.FragileCar;

import java.awt.Point;
import java.util.List;

/**
 * Created by Pontus on 2016-04-13.
 */
public interface DrawableBot {
    List<Point> getWallPoints();
    List<Point> getCheckPoints();
    int getWallThreshold();
    int getStickLength();
    int getStickX();
    int getStickY();

    FragileCar getCar();
}

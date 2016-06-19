package model.carcontrollers;

import model.carcontrollers.util.WallPoint;
import model.cars.FragileCar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pontu on 2016-04-08.
 */
public class CheckBot implements CarController, DrawableBot{

    FragileCar car;
    List<WallPoint> checkPoints = new ArrayList<>();

    public CheckBot(FragileCar car, String trackName){
        this.car = car;
    }

    @Override
    public void update(double deltaTime) {
        car.accelerate();
    }

    @Override
    public List<WallPoint> getWallPoints() {
        return checkPoints;
    }

    @Override
    public int getWallThreshold() {
        return 0;
    }

    @Override
    public int getStickLength() {
        return 0;
    }

    @Override
    public int getStickX() {
        return 0;
    }

    @Override
    public int getStickY() {
        return 0;
    }

    @Override
    public FragileCar getCar() {
        return car;
    }
}

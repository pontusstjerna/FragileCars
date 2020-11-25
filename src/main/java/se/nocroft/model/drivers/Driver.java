package se.nocroft.model.drivers;

import se.nocroft.model.GameObject;
import se.nocroft.model.cars.FragileCar;

import java.awt.*;

public abstract class Driver implements GameObject {
    protected FragileCar car;
    protected String trackName;

    public Driver(FragileCar car, String trackName) {
        this.car = car;
        this.trackName = trackName;
    }

    @Override
    public void update(double deltaTime) {
        // TODO
        // Gasa, bromsa, etc

        car.accelerate();
        car.turnLeft(deltaTime);
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {

    }
}

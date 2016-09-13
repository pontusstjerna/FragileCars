package model.carcontrollers;

import model.GameObject;
import model.cars.FragileCar;

import java.awt.*;

/**
 * Created by pontu on 2016-06-22.
 */
public class AlfredBot implements GameObject {

    private FragileCar car;

    //Alfreds asvackra konstruktor
    public AlfredBot(FragileCar car, String trackName){
        this.car = car;

        //Konstruktor-kod
    }

    @Override
    public void update(double deltaTime) {
        //TODO
        //Gasa, bromsa, etc

        car.accelerate();
        car.turnLeft(deltaTime);
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {

    }
}

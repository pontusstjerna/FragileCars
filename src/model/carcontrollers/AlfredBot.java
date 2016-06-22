package model.carcontrollers;

import model.cars.FragileCar;
/**
 * Created by pontu on 2016-06-22.
 */
public class AlfredBot implements CarController{

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
}

package model.carcontrollers;

import model.GameObject;
import model.cars.FragileCar;

import java.awt.*;

/**
 * Created by pontu on 2016-09-29.
 */
public class TapeBot implements GameObject {

    private FragileCar car;

    public TapeBot(FragileCar car, String trackName){
        this.car = car;

    }

    @Override
    public void update(double deltaTime) {
        
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {

    }
}

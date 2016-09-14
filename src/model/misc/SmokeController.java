package model.misc;

import model.GameObject;
import model.cars.DrawableCar;
import model.cars.FragileCar;

import java.awt.*;
import java.util.Random;

/**
 * Created by pontu on 2016-09-12.
 */
public class SmokeController implements GameObject {

    FragileCar car;

    private final int MAX_SMOKE = 200;
    private int index = 0;
    private Random rand;

    //Round buffer
    private SmokeParticle[] smoke = new SmokeParticle[MAX_SMOKE];


    public SmokeController(FragileCar car){
        this.car = car;
        rand = new Random();
    }

    @Override
    public void update(double deltaTime) {
        if(smoke[index] == null){
            smoke[index] = new SmokeParticle(car.getX(), car.getY(), car.getAcceleration(), car.getHeading(), rand);
        }else{
            smoke[index].reSet(car.getX(), car.getY(), car.getAcceleration(), car.getHeading());
        }
        index = (index + 1) % MAX_SMOKE;

        //Update all particles
        for(SmokeParticle s : smoke){
            if(s != null)
                s.update(deltaTime);
        }
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        for(SmokeParticle s : smoke){
            if(s != null)
                s.paint(g, scale, scaleX);
        }
    }
}

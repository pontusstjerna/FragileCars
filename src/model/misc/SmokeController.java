package model.misc;

import model.GameObject;
import model.cars.DrawableCar;

import java.awt.*;
import java.util.Random;

/**
 * Created by pontu on 2016-09-12.
 */
public class SmokeController implements GameObject {

    DrawableCar car;

    private final int MAX_SMOKE = 500;
    private int index = 0;
    private Random rand;

    //Round buffer
    private SmokeParticle[] smoke = new SmokeParticle[MAX_SMOKE];


    public SmokeController(DrawableCar car){
        this.car = car;
        rand = new Random();
    }

    @Override
    public void update(double deltaTime) {
        smoke[index] = new SmokeParticle(car.getX(), car.getY(), rand);
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

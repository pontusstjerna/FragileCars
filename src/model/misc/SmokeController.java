package model.misc;

import model.GameObject;
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

        for(int i = 0; i < MAX_SMOKE; i++){
            smoke[i] = new SmokeParticle(car.getX(), car.getY(), car.getAcceleration(), car.getHeading(), rand);
        }
    }

    @Override
    public void update(double deltaTime) {
        smoke[index].reSet(car.getX(), car.getY(), car.getAcceleration(), car.getHeading());

        index = (index + 1) % MAX_SMOKE;

        //Update all particles
        for(SmokeParticle s : smoke) {
            s.update(deltaTime);
        }
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        //TODO: Getting some lag spikes and fps drops to 4 fps sometimes, need to fix that

        //Paint all particles
        for(SmokeParticle s : smoke) {
            s.paint(g, scale, scaleX);
        }

        /*//Paint all particles
        for(int i = 0; i < MAX_SMOKE; i += 5) {
            smoke[i].paint(g, scale, scaleX);
        }*/
    }
}

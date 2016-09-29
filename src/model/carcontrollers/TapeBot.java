package model.carcontrollers;

import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.cars.FragileCar;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by pontu on 2016-09-29.
 */
public class TapeBot implements GameObject {

    private enum Dir {STRAIGHT, LEFT, RIGHT}

    private FragileCar car;
    private boolean onTape = false;
    private Dir dir = Dir.STRAIGHT;
    private final int UPDATE_INTERVAL = 400;
    private int time = 0;
    private Random rand;

    private ArrayList<BotPoint> tape;

    public TapeBot(FragileCar car, String trackName){
        this.car = car;
        rand = new Random();
        tape = new ArrayList<>();
    }

    @Override
    public void update(double deltaTime) {
        if(car.getAcceleration() < 100) car.accelerate();
        turn(deltaTime);
        time += deltaTime*1000;
        if(!onTape){
            discover();
        }else{
            followTape();
        }
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        if(car.getName().equals("BLUE car")){
            g.setColor(Color.BLUE);
        }else if(car.getName().equals("GREEN car"))
        {
            g.setColor(Color.GREEN);
        }else if(car.getName().equals("RED car")){
            g.setColor(Color.RED);
        }else if(car.getName().equals("YELLOW car")){
            g.setColor(Color.YELLOW);
        }

        //Paint balls
        for(BotPoint p : tape){
            int s = (int)(scale*10);
            g.fillRoundRect((int)(p.x*scale)-(s/2) + scaleX, (int)(p.y*scale)-(s/2), s,s,s,s);
            int dist = (int)(scale*p.getRadius()*2);
            g.drawRoundRect((int)(p.x*scale)-(dist/2) + scaleX, (int)(p.y*scale)-(dist/2),
                    dist, dist, dist, dist);
        }
    }

    private void discover(){
        if(time > UPDATE_INTERVAL){
            switch(rand.nextInt(3)){
                case 0:
                    dir = Dir.STRAIGHT;
                    break;
                case 1:
                    dir = Dir.LEFT;
                    break;
                case 2:
                    dir = Dir.RIGHT;
                    break;
            }
            time = 0;
            addTape();
        }
    }

    private void addTape(){
        int x = car.getWidth()/2;
        int y = car.getHeight();
        tape.add(new BotPoint(car.getRelX(x, y), car.getRelY(x, y), car.getHeight()));
    }

    private void followTape(){

    }

    private void turn(double dTime){
        switch(dir){
            case LEFT:
                car.turnLeft(dTime);
                break;
            case RIGHT:
                car.turnRight(dTime);
                break;
        }
    }
}

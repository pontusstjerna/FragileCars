package model.carcontrollers;

import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.cars.Car;
import model.cars.FragileCar;
import util.CfgParser;

import java.awt.*;
import java.util.*;

/**
 * Created by pontu on 2016-09-29.
 */
public class WallTapeBot implements GameObject {

    private enum Dir {STRAIGHT, LEFT, RIGHT}

    private FragileCar car;
    private boolean onTape = true;
    private Dir dir = Dir.STRAIGHT;
    private Random rand;
    private boolean debugMode = false;
    private int lastX, lastY;
    private boolean savedMode = false;
    private double time = 0;
    private final int UPDATE_INTERVAL = 300;
    private int state = 0;
    private int dynStickLength;

    private ArrayList<BotPoint> walls;

    //These two will be mutated
    private Point leftStick, rightStick;

    private final int STICK_LENGTH = 150;
    private final int CRASH_RADIUS = 50;

    public WallTapeBot(FragileCar car, String trackName){
        this.car = car;
        rand = new Random();
        walls = new ArrayList<>();

        leftStick = new Point(0,0);
        rightStick = new Point(0,0);

        dynStickLength = STICK_LENGTH;

        lastX = (int)car.getMiddleX(car.getX());
        lastY = (int)car.getMiddleY(car.getY());

        debugMode = new CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled");
    }

    @Override
    public void update(double deltaTime) {
        turn(deltaTime);
        updateSticks();
        checkReset();
        time += deltaTime*1000;
        if(!onTape){
            discover();
            avoidTape();
        }else{
            avoidTape();
        }
        lastX = (int)car.getMiddleX(car.getX());
        lastY = (int)car.getMiddleY(car.getY());
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        if(debugMode){
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
            for(BotPoint p : walls){

                int s = (int)(scale*10);
                g.fillRoundRect((int)(p.x*scale)-(s/2) + scaleX, (int)(p.y*scale)-(s/2), s,s,s,s);
                int dist = (int)(scale*p.getRadius()*2);
                g.drawRoundRect((int)(p.x*scale)-(dist/2) + scaleX, (int)(p.y*scale)-(dist/2),
                        dist, dist, dist, dist);
            }


            //Paint sticks
            int leftStickX = (int)(leftStick.x*scale) + scaleX;
            int leftStickY = (int)(leftStick.y*scale);

            g.drawLine((int)(car.getMiddleX(car.getX())*scale) + scaleX, (int)(car.getMiddleY(car.getY())*scale),
                    leftStickX, leftStickY);

            int rightStickX = (int)(rightStick.x*scale) + scaleX;
            int rightStickY = (int)(rightStick.y*scale);

            g.drawLine((int)(car.getMiddleX(car.getX())*scale) + scaleX, (int)(car.getMiddleY(car.getY())*scale),
                    rightStickX, rightStickY);
        }
    }

    private void discover(){
        if(time > UPDATE_INTERVAL){
            actOnState(5);
            time = 0;
        }
        setSpeed(300);
    }

    private void actOnState(int state){
        switch(state) {
            case 0:
                dir = Dir.STRAIGHT;
                break;
            case 1:
                dir = Dir.LEFT;
                break;
            case 2:
                dir = Dir.RIGHT;
                break;
            default:
                turnRandom();
                break;
        }
    }

    private void turnRandom(){
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
    }

    private void avoidTape() {
        boolean onTape = true;

        if(onTape(leftStick.x, leftStick.y) && onTape(rightStick.x, rightStick.y)){ //If on tape, just keep driving in the direction we were to avoid
            setSpeed(50);

            //If both sticks are inside tape, decrease their length and see if still inside and continue until one is outside
            if(dynStickLength > 10){
                dynStickLength -= 2;
                updateSticks();
                avoidTape();
            }
        }else if(onTape(leftStick.x, leftStick.y)){
            dir = Dir.RIGHT;
            setSpeed(50);
        }else if(onTape(rightStick.x, rightStick.y)){
            dir = Dir.LEFT;
            setSpeed(50);
        }else{
            onTape = false;
            //car.brake();
        }

        if(dynStickLength < STICK_LENGTH){
            dynStickLength++;
        }
        this.onTape = onTape;
    }

    private boolean onTape(int x, int y){
        for(BotPoint p : walls){
            if(p.distance(x,y) < p.getRadius()){
                return true;
            }
        }
        return false;
    }

    private void updateSticks(){
        leftStick.x = car.getRelX(0, -dynStickLength);
        leftStick.y = car.getRelY(0, -dynStickLength);
        rightStick.x = car.getRelX(car.getWidth(), -dynStickLength);
        rightStick.y = car.getRelY(car.getWidth(), -dynStickLength);
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

    private void setSpeed(int speed){
        if(car.getAcceleration() < speed){
            car.accelerate();
        }
    }

    private void checkReset(){
        if(Point.distance(car.getMiddleX(car.getX()), car.getMiddleY(car.getY()), lastX, lastY) > 50 && !savedMode){
            reset();
        }
    }

    private void reset(){
        addCrash();
        incState();
        dynStickLength = STICK_LENGTH;
        onTape = false;
    }

    private void addCrash(){
        //Add new crash to walls
        walls.add(new BotPoint(lastX, lastY, CRASH_RADIUS));
    }

    private ArrayList<BotPoint> getCurrentTape(int x, int y){
        ArrayList<BotPoint> pts = new ArrayList<>();
        for(BotPoint p : walls){
            if(p.distance(x,y) < p.getRadius()){
                pts.add(p);
            }
        }
        return pts;
    }

    private ArrayList<BotPoint> getOverlapTape(int x, int y, int radius){
        ArrayList<BotPoint> pts = new ArrayList<>();
        for(BotPoint p : walls){
            if(p.distance(x,y) - radius < p.getRadius()){
                pts.add(p);
            }
        }
        return pts;
    }

    private void incState(){
        state = (state + 1) % 4;
    }

    private void cleanTape(){
        for(int i = 0; i < walls.size(); i++){
            for(int j = 0; j < walls.size(); j++){
                if(walls.get(i).distance(walls.get(j)) < walls.get(i).getRadius()/10){
                    walls.remove(walls.get(j));
                }
            }
        }
    }

    private void saveLap(){
        if(savedMode && car.getFinished() != 0){
            saveLapToFile();
        }
    }

    private void saveLapToFile(){
        System.out.println("Saving " + car.getName() + "'s tape to file for this track.");
    }
}

package model.carcontrollers;

import com.sun.istack.internal.Nullable;
import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.cars.FragileCar;
import util.CfgParser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by pontu on 2016-09-29.
 */
public class TapeBot implements GameObject {

    private enum Dir {STRAIGHT, LEFT, RIGHT}

    private FragileCar car;
    private boolean onTape = true;
    private Dir dir = Dir.STRAIGHT;
    private final int UPDATE_INTERVAL = 100;
    private int time = 0;
    private Random rand;
    private boolean debugMode = false;
    private int lastX, lastY;

    private ArrayList<BotPoint> tape;

    public TapeBot(FragileCar car, String trackName){
        this.car = car;
        rand = new Random();
        tape = new ArrayList<>();

        debugMode = new CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled");

        //makeTape(trackName)
    }

    @Override
    public void update(double deltaTime) {
        turn(deltaTime);
        time += deltaTime*1000;
        checkReset();
        if(onTape){
            followTape();
        }else{
            discover();
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
            for(BotPoint p : tape){
                int s = (int)(scale*10);
                g.fillRoundRect((int)(p.x*scale)-(s/2) + scaleX, (int)(p.y*scale)-(s/2), s,s,s,s);
                int dist = (int)(scale*p.getRadius()*2);
                g.drawRoundRect((int)(p.x*scale)-(dist/2) + scaleX, (int)(p.y*scale)-(dist/2),
                        dist, dist, dist, dist);
            }
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
        car.accelerate();
    }

    private void addTape(){
        int x = car.getWidth()/2;
        int y = car.getHeight();
        int radius = 50;
        tape.add(new BotPoint(car.getRelX(x, y), car.getRelY(x, y), radius));
    }

    private void followTape() {
        int leftX = car.getRelX(0, 0);
        int leftY = car.getRelY(0, 0);
        int rightX = car.getRelX(car.getWidth(), 0);
        int rightY = car.getRelY(car.getWidth(), 0);

        if(car.getAcceleration() < 300) car.accelerate();
        if(onTape(leftX, leftY) && onTape(rightX, rightY)){
            dir = Dir.STRAIGHT;
        }else if(onTape(leftX, leftY)){
            dir = Dir.LEFT;
        }else if(onTape(rightX, rightY)){
            dir = Dir.RIGHT;
        }else{
            onTape = false;
            System.out.println("Off tape");
            //car.brake();
        }
    }

    private boolean onTape(int x, int y){
        for(BotPoint p : tape){
            if(p.distance(x,y) < p.getRadius()){
                return true;
            }
        }
        return false;
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

    private void checkReset(){
        if(Point.distance(car.getMiddleX(car.getX()), car.getMiddleY(car.getY()), lastX, lastY) > 100){
            onTape = true;
            removeTape();
        }
    }

    private void removeTape(){
        //Remove the tape that we died in
        for(BotPoint p : getCurrentTape(lastX, lastY)){
            tape.remove(p);
        }

        //Remove one more to not get stuck
        if(tape.size() > 0){
            tape.remove(tape.size()-1);
        }
    }

    private ArrayList<BotPoint> getCurrentTape(int x, int y){
        ArrayList<BotPoint> pts = new ArrayList<>();
        for(BotPoint p : tape){
            if(p.distance(x,y) < p.getRadius()){
                pts.add(p);
            }
        }

        System.out.println(pts.size());
        return pts;
    }

    private void makeTape(String trackName){
        switch(trackName){
            case "2":
                int h = 60;
                tape.add(new BotPoint(1150, 500, h));
                tape.add(new BotPoint(1150, 400, h));
                tape.add(new BotPoint(1150, 300, h));
                tape.add(new BotPoint(1100, 200, h));
                tape.add(new BotPoint(1050, 150, h));
                tape.add(new BotPoint(980, 150, h));
                tape.add(new BotPoint(900, 150, h));
                tape.add(new BotPoint(800, 155, h));
                tape.add(new BotPoint(700, 150, h));
                tape.add(new BotPoint(600, 150, h));
                tape.add(new BotPoint(500, 150, h));
                tape.add(new BotPoint(400, 160, h));
                tape.add(new BotPoint(300, 160, h));
                tape.add(new BotPoint(200, 160, h));
                tape.add(new BotPoint(150, 260, h));
                tape.add(new BotPoint(160, 360, h));
                tape.add(new BotPoint(170, 430, h));
                tape.add(new BotPoint(270, 460, h));
                tape.add(new BotPoint(370, 460, h));
                tape.add(new BotPoint(470, 460, h));
                tape.add(new BotPoint(570, 460, h));
                tape.add(new BotPoint(670, 460, h));
                tape.add(new BotPoint(750, 500, h));
                tape.add(new BotPoint(800, 600, h));
                tape.add(new BotPoint(700, 700, h));
                tape.add(new BotPoint(600, 750, h));
                tape.add(new BotPoint(500, 750, h));
                tape.add(new BotPoint(400, 750, h));
                tape.add(new BotPoint(300, 750, h));
                tape.add(new BotPoint(200, 750, h));
                tape.add(new BotPoint(180, 800, h));
                tape.add(new BotPoint(150, 900, h));
                tape.add(new BotPoint(160, 1000, h));
                tape.add(new BotPoint(180, 1050, h));
                tape.add(new BotPoint(280, 1105, h));
                tape.add(new BotPoint(380, 1105, h));
                tape.add(new BotPoint(480, 1105, h));
                tape.add(new BotPoint(580, 1105, h));
                tape.add(new BotPoint(680, 1105, h));
                tape.add(new BotPoint(780, 1105, h));
                tape.add(new BotPoint(880, 1105, h));
                tape.add(new BotPoint(980, 1105, h));
                tape.add(new BotPoint(1080, 1105, h));
                tape.add(new BotPoint(1080, 1105, h));
                tape.add(new BotPoint(1080, 1105, h));
                tape.add(new BotPoint(1100, 1050, h));
                tape.add(new BotPoint(1110, 1100, h));
                tape.add(new BotPoint(1110, 1000, h));
                tape.add(new BotPoint(1120, 900, h));
                tape.add(new BotPoint(1130, 800, h));
                tape.add(new BotPoint(1140, 700, h));
                tape.add(new BotPoint(1150, 600, h));
                break;
        }
    }
}

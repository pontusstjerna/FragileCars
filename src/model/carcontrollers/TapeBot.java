package model.carcontrollers;

import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.carcontrollers.util.TapePiece;
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
    private int lastTapeLength;
    private int oldTapeLength;
    private int state = 0;
    private int tapeIndex, lastTapeIndex = 0;
    private boolean followMode = false;

    private final int WEIGHT_LIMIT = 100;

    private ArrayList<TapePiece> tape;
    private ArrayList<ArrayList<TapePiece>> tapes;

    public TapeBot(FragileCar car, String trackName){
        this.car = car;
        rand = new Random();
        tape = new ArrayList<>();

        lastX = (int)car.getMiddleX(car.getX());
        lastY = (int)car.getMiddleY(car.getY());

        debugMode = new CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled");

        //makeTape(trackName)
    }

    @Override
    public void update(double deltaTime) {
        turn(deltaTime);
        time += deltaTime*1000;
        checkReset();
        checkForCycles();
        if(!followMode){
            if(!onTape){
                discover();
                followTape(tape);
            }else{
                followTape(tape);
            }
            lastX = (int)car.getMiddleX(car.getX());
            lastY = (int)car.getMiddleY(car.getY());
            saveLap();
        }else{
            followTape(tapes.get(0));
        }

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
            actOnState(state);
            addTape();
            time = 0;
        }
        car.accelerate();
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

    private void addTape(){
        int x = car.getWidth()/2;
        int y = car.getHeight();
        int radius = 50;
        tape.add(new TapePiece(car.getRelX(x, y), car.getRelY(x, y), radius));
    }

    private void followTape(ArrayList<TapePiece> tape) {
        int leftX = car.getRelX(0, 0);
        int leftY = car.getRelY(0, 0);
        int rightX = car.getRelX(car.getWidth(), 0);
        int rightY = car.getRelY(car.getWidth(), 0);
        boolean onTape = true;

        if(car.getAcceleration() < 300) car.accelerate();

        if(onTape(leftX, leftY) && onTape(rightX, rightY)){
            dir = Dir.STRAIGHT;
        }else if(onTape(leftX, leftY)){
            dir = Dir.LEFT;
        }else if(onTape(rightX, rightY)){
            dir = Dir.RIGHT;
        }else{
            onTape = false;
            //car.brake();
        }

        this.onTape = onTape;
    }

    private boolean onTape(int x, int y){
        for(TapePiece p : tape){
            if(p.distance(x,y) < p.getRadius()){
                tapeIndex = tape.indexOf(p);
                p.incWeight();
                return true;
            }
        }
        return false;
    }

    private void checkForCycles(){
      //  System.out.println(tapeIndex);
        int indexDiff = Math.abs(lastTapeIndex - tapeIndex);
        if(indexDiff > 10){
            rollBack(indexDiff);
            incState();
        }
        lastTapeIndex = tapeIndex;
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
        if(Point.distance(car.getMiddleX(car.getX()), car.getMiddleY(car.getY()), lastX, lastY) > 50){
            onTape = true;
            removeTape();
            incState();
            lastTapeLength = tape.size();
            tapeIndex = 0;
            lastTapeIndex = 0;
            followMode = false;
           // cleanTape();
            checkProgress();
        }
    }

    private void checkProgress(){
        if(state == 0){
            //Check if stuck or in a loop/cycle
            if(Math.abs(tape.size() - oldTapeLength) < 3){
                rollBack(20);
            }
            //System.out.println("tapeSize: " + tape.size() + " oldTapeLength: " + oldTapeLength + " abs: " + Math.abs(tape.size() - oldTapeLength));
            oldTapeLength = tape.size();
        }
    }

    private void rollBack(int nPoints){
        int length = tape.size();
        for(int i = tape.size()-1; i > length - nPoints && tape.size() > 1 && i > 0; i--){
            if(tape.get(i).getWeight() < WEIGHT_LIMIT){ //Remove if not safe enough
                tape.remove(i);
            }else{
                tape.get(i).decWeight();
                tape.get(i).decWeight();
            }
        }
        System.out.println("Rollback -" + nPoints + " pts for " + car.getName());
    }

    private void removeTape(){

        //Require at least 3 new tape points, otherwise remove it
        while(Math.abs(tape.size() - lastTapeLength) < 4 && tape.size() > 1){
            tape.remove(tape.size()-1);
        }

        //Remove the tape that we died in
        for(TapePiece p : getCurrentTape(lastX, lastY)){
            if(p.getWeight() < WEIGHT_LIMIT)
            tape.remove(p);
        }
    }

    private ArrayList<TapePiece> getCurrentTape(int x, int y){
        ArrayList<TapePiece> pts = new ArrayList<>();
        for(TapePiece p : tape){
            if(p.distance(x,y) < p.getRadius()){
                pts.add(p);
            }
        }
        return pts;
    }

    private void incState(){
        state = (state + 1) % 4;
    }

    private void cleanTape(){
        for(int i = 0; i < tape.size(); i++){
            for(int j = 0; j < tape.size(); j++){
                if(tape.get(i).distance(tape.get(j)) < tape.get(i).getRadius()/10){
                    tape.remove(tape.get(j));
                }
            }
        }
    }

    private void saveLap(){
        if(tapes == null && car.getLaps() > 0){
            tapes = new ArrayList<>();
            tapes.add(tape);
            followMode = true;
        }else if(tapes != null && car.getLaps() > tapes.size()){ //New lap
            tapes.add(tape);
        }
    }
}

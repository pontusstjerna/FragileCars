package model.carcontrollers;

import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.cars.FragileCar;
import util.CfgParser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;

/**
 * Created by pontu on 2016-09-29.
 */
public class TapeBot implements GameObject {

    private enum Dir {STRAIGHT, LEFT, RIGHT}
    private enum States {STRAIGHT, LEFT, RIGHT, RANDOM1, WEAK_LEFT, WEAK_RIGHT}

    private FragileCar car;
    private boolean onTape = false;
    private Dir dir = Dir.STRAIGHT;
    private final int SPAWN_INTERVAL = 100;
    private int tapeTime = 0;
    private int turnTime = 0;
    private Random rand;
    private boolean debugMode = false;
    private int lastX, lastY;
    private int lastMainTapeLength;
    private States state = States.STRAIGHT;
    private boolean lastOnTape = false;
    private boolean followMode = false;
    private boolean suicide = false;

    private ArrayList<BotPoint> mainTape;
    private Stack<BotPoint> tapeStack;
    

    public TapeBot(FragileCar car, String trackName){
        this.car = car;
        rand = new Random();
        mainTape = new ArrayList<>();
        tapeStack = new Stack<>();

        lastX = (int)car.getMiddleX(car.getX());
        lastY = (int)car.getMiddleY(car.getY());

        debugMode = new CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled");

        //makeTape(trackName)
    }

    @Override
    public void update(double deltaTime) {
        if(suicide){
            car.accelerate();
            checkReset();
            System.out.println("SUICIDE");
            return;
        }
        turn(deltaTime);
        tapeTime += deltaTime*1000;
        turnTime += deltaTime*1000;
        checkForCycles();
        checkReset();
        if(!followMode){
            if(!onTape){
                discover();
                followTape(mainTape);
            }else{
                followTape(mainTape);
            }
            lastX = (int)car.getMiddleX(car.getX());
            lastY = (int)car.getMiddleY(car.getY());
            saveLap();
        }else{
            followTape(mainTape);
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

            //Paint mainTape
            for(BotPoint p : mainTape){
                int s = (int)(scale*10);
                g.fillRoundRect((int)(p.x*scale)-(s/2) + scaleX, (int)(p.y*scale)-(s/2), s,s,s,s);
                int dist = (int)(scale*p.getRadius()*2);
                g.drawRoundRect((int)(p.x*scale)-(dist/2) + scaleX, (int)(p.y*scale)-(dist/2),
                        dist, dist, dist, dist);
            }
            //Paint tapeStack with only the peripheral
            for(BotPoint p : tapeStack){
                int s = (int)(scale*10);
                int dist = (int)(scale*p.getRadius()*2);
                g.drawRoundRect((int)(p.x*scale)-(dist/2) + scaleX, (int)(p.y*scale)-(dist/2),
                        dist, dist, dist, dist);
            }
        }
    }

    private void discover(){
        if(tapeTime > SPAWN_INTERVAL){
            addTape();
            tapeTime = 0;
        }
        actOnState(state);
        car.accelerate();
    }

    private void actOnState(States state){
        switch(state) {
            case STRAIGHT:
                dir = Dir.STRAIGHT;
                break;
            case LEFT:
                dir = Dir.LEFT;
                break;
            case RIGHT:
                dir = Dir.RIGHT;
                break;
            case RANDOM1:
                turnRandom(400);
                break;
            case WEAK_LEFT:
                turnDynamic(Dir.LEFT, 50);
                break;
            case WEAK_RIGHT:
                turnDynamic(Dir.RIGHT, 50);
                break;
        }
    }

    private void turnRandom(int interval){
        if(turnTime > interval){
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
            turnTime = 0;
        }
    }

    private void turnDynamic(Dir dir, int interval){
        if(turnTime > interval){
            this.dir = dir;
            turnTime = 0;
        }else{
            this.dir = Dir.STRAIGHT;
        }
    }

    private void addTape(){
        final int radius = 50;
        final int x = car.getWidth()/2;
        final int y = 0;
        tapeStack.push(new BotPoint((int)car.getRelX(x,y), (int)car.getRelY(x, y), radius));
    }

    private void followTape(ArrayList<BotPoint> tape) {
        int leftX = car.getRelX(0, 0);
        int leftY = car.getRelY(0, 0);
        int rightX = car.getRelX(car.getWidth(), 0);
        int rightY = car.getRelY(car.getWidth(), 0);
        boolean onTape = true;

        if(car.getAcceleration() < 300) car.accelerate();

        if(onTape(mainTape, leftX, leftY) && onTape(mainTape, rightX, rightY)){
            dir = Dir.STRAIGHT;
        }else if(onTape(mainTape, leftX, leftY)){
            dir = Dir.LEFT;
        }else if(onTape(mainTape, rightX, rightY)){
            dir = Dir.RIGHT;
        }else{
            onTape = false;
            //car.brake();
        }

        this.onTape = onTape;
    }

    private boolean onTape(Collection<BotPoint> tape, int x, int y){
        for(BotPoint p : tape){
            if(p.distance(x,y) < p.getRadius()){
                return true;
            }
        }
        return false;
    }

    private void checkForCycles(){

        //If the car, while building its stack of new tape, runs over the main tape,
        //THERE IS DEFINITELY A CYCLE.
        if(onTape && !lastOnTape){
            tapeStack.clear();
            suicide = true;
        }
        lastOnTape = onTape;
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
            reset();
        }
    }

    private void reset(){
        if(suicide){
            state = incState(state);
            suicide = false;
            return;
        }
        state = incState(state);
        removeTape();
        followMode = false;
        onTape = true;
        lastOnTape = onTape;
        // cleanTape();
        if(!glueTape()){
            checkProgress();
        }
        lastMainTapeLength = mainTape.size();
        System.out.println("State for " + car.getName() + ": " + state);
    }


    private void checkProgress(){
        if(state == States.STRAIGHT){
            //Check if not enough progress has been made
            if(mainTape.size() - lastMainTapeLength < 3){
                rollBack(10);
            }
            //System.out.println("tapeSize: " + mainTape.size() + " oldTapeLength: " + oldTapeLength + " abs: " + Math.abs(mainTape.size() - oldTapeLength));
        }
    }

    private void rollBack(int nPoints){
        int length = mainTape.size();
        for(int i = mainTape.size()-1; i > length - nPoints && i >= 0; i--){
            mainTape.remove(i);
        }
        System.out.println("Rollback -" + nPoints + " pts for " + car.getName());
    }


    private boolean glueTape(){
        if(tapeStack.size() > 0){
            mainTape.addAll(tapeStack);
            tapeStack.clear();
            //Start a new state cycle
            state = States.STRAIGHT;
            return true;
        }
        return false;
    }

    private void removeTape(){

        //Require at least 3 new mainTape points, otherwise remove it
        if(tapeStack.size() < 3 ){
            tapeStack.clear();
        }else{
            //Remove the new tape from stack that we died in
            while(onTape(tapeStack, lastX, lastY)){
                tapeStack.pop();
            }
            //Always step back a little
            final int stackRollback = 3;
            for(int i = 0; i < stackRollback && !tapeStack.empty(); i++){
                tapeStack.pop();
            }
        }
    }

    private ArrayList<BotPoint> getCurrentTape(int x, int y){
        ArrayList<BotPoint> pts = new ArrayList<>();
        for(BotPoint p : mainTape){
            if(p.distance(x,y) < p.getRadius()){
                pts.add(p);
            }
        }
        return pts;
    }

    private States incState(States state){
        return States.values()[(state.ordinal() + 1) % States.values().length];
    }

    private double getDiff(double a, double b){
        return Math.abs(a - b);
    }

    private void saveLap(){
        if(car.getLaps() > 0){
            followMode = true;
        }
        //TODO: Save lap
    }
}

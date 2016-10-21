package model.carcontrollers;

import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.carcontrollers.util.Lap;
import model.cars.Car;
import model.cars.FragileCar;
import util.CfgParser;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;

/**
 * Created by pontu on 2016-09-29.
 */
public class TapeBot implements GameObject {

    private enum Dir {STRAIGHT, LEFT, RIGHT}
    private enum States {STRAIGHT, LEFT, RIGHT, RANDOM1, WEAK_LEFT, WEAK_RIGHT, SHARPER_LEFT, SHARPER_RIGHT}

    private FragileCar car;
    private States state = States.STRAIGHT;
    private Dir dir = Dir.STRAIGHT;
    private Random rand;
    private int tapeTime = 0;
    private int turnTime = 0;
    private int speedLimit = 300;
    private int lastX, lastY;
    private int lastMainTapeLength;
    private int lastLap = 0;
    private boolean onTape = false;
    private boolean debugMode = false;
    private boolean lastOnTape = false;
    private boolean followMode;
    private boolean suicide = false;
    private boolean altState = false;
    private boolean finished = false;
    private boolean runOnLoaded = false;
    private boolean passedGoalLine = false;

    private ArrayList<BotPoint> mainTape;
    private Stack<BotPoint> tapeStack;

    private final String trackName;
    

    public TapeBot(FragileCar car, String trackName){
        this.car = car;
        mainTape = new ArrayList<>();
        this.trackName = trackName;
        if(tryLoadLap()){
            followMode = true;
        }else{
            followMode = false;
        }
        runOnLoaded = followMode;

        rand = new Random();
        tapeStack = new Stack<>();

        lastX = (int)car.getMiddleX(car.getX());
        lastY = (int)car.getMiddleY(car.getY());

        debugMode = new CfgParser(CfgParser.STD_PATH).readBoolean("debugEnabled");

        //makeTape(trackName)
    }

    @Override
    public void update(double deltaTime) {
        if(finished) return;

        turn(deltaTime);
        tapeTime += deltaTime*1000;
        turnTime += deltaTime*1000;
        if(!followMode){
            checkForCycles();
            if(checkReset()) reset();
            if(!onTape){
                discover();
                followTape(mainTape);
            }else{
                followTape(mainTape);
            }
            lastX = (int)car.getMiddleX(car.getX());
            lastY = (int)car.getMiddleY(car.getY());
            lockLap();
        }else{
            followTape(mainTape);

            //Check if passed goalline
            if(car.getLaps() > lastLap){
                passedGoalLine = true;
                lastLap = car.getLaps();
            }
            //If we have not loaded a saved track, try to find the right speed for it
            if(!runOnLoaded && checkReset() && !passedGoalLine) slowDown();
            if(car.getFinished() != 0){
                saveLap();
            }
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
        final int SPAWN_INTERVAL = 100;
        if(tapeTime > SPAWN_INTERVAL){
            addTape();
            tapeTime = 0;
        }
        actOnState(state);
        car.accelerate();
    }

    private void actOnState(States state){
        if(altState){//Alternate the order of left/right when stuck too long
            if(state == States.LEFT){
                state = States.RIGHT;
            }else if(state == States.RIGHT){
                state = States.LEFT;
            }
        }
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
                turnDynamic(Dir.LEFT, 30);
                break;
            case WEAK_RIGHT:
                turnDynamic(Dir.RIGHT, 30);
                break;
            case SHARPER_LEFT:
                turnDynamic(Dir.RIGHT, 5);
                break;
            case SHARPER_RIGHT:
                turnDynamic(Dir.LEFT, 5);
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

        //Keep speed when in tape to not fall out
        if(car.getAcceleration() < speedLimit) car.accelerate();

        if(onTape(tape, leftX, leftY) && onTape(tape, rightX, rightY)){
            dir = Dir.STRAIGHT;
        }else if(onTape(tape, leftX, leftY)){
            dir = Dir.LEFT;
        }else if(onTape(tape, rightX, rightY)){
            dir = Dir.RIGHT;
        }else{
            onTape = false;
            if(followMode) dir = Dir.STRAIGHT;
        }

        this.onTape = onTape;
    }

    //Are we on any tape at the given coordinate?
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

    private boolean checkReset(){
        return Point.distance(car.getMiddleX(car.getX()), car.getMiddleY(car.getY()), lastX, lastY) > 50;
    }

    private void reset(){
        state = incState(state);
        removeTape();
        followMode = false;
        onTape = true;
        lastOnTape = true;
        if(!glueTape()){
            checkProgress();
        }
        if(suicide) suicide = false;
        lastMainTapeLength = mainTape.size();
        System.out.println("State for " + car.getName() + ": " + state);
    }

    private void checkProgress(){
        if(state == States.STRAIGHT){
            //Check if not enough progress has been made
            if(mainTape.size() - lastMainTapeLength < 3){
                rollBack(20);
                altState = !altState;
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
        if(tapeStack.size() < 3 || suicide){
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

    private States incState(States state){
        return States.values()[(state.ordinal() + 1) % States.values().length];
    }

    private void lockLap(){
        if(car.getLaps() > 0) {
            lastLap = car.getLaps();
            followMode = true;
            passedGoalLine = true;
            //Try to go as fast as possible, then when dying, slow down until speed is perfect
            speedLimit = Car.speedLimit;
            glueTape();
        }
    }

    private void slowDown(){
        //Slow down until speed is nice
        if(speedLimit > 50){
            speedLimit -= 50;
        }else{ //Should never get here but if it does there is something seriously wrong with our lap
            followMode = false;
            //Reset everything
            mainTape.clear();
            tapeStack.clear();
        }
    }

    private boolean tryLoadLap(){
        try{
            String fileName = car.getName() + "_" + trackName + ".lap";
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Lap lap = (Lap) ois.readObject();
            ois.close();

            mainTape = lap.getLap();
            speedLimit = lap.getSpeedLimit();
            return true;
        }catch(FileNotFoundException e){
            System.out.println("No saved lap found for " + car.getName() + " on track " + trackName + ".");
        }catch(IOException e){
            System.out.println("Unable to load file.");
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }

    private void saveLap(){
        if(!runOnLoaded){ //Don't save if it's already saved
            try{
                String fileName = car.getName() + "_" + trackName + ".lap";
                File yourFile = new File(fileName);
                yourFile.createNewFile(); // if file already exists will do nothing
                FileOutputStream fos = new FileOutputStream(fileName, false);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(new Lap(mainTape, speedLimit));
                oos.close();

                finished = true;
                System.out.println("File " + fileName + " saved successfully.");
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e) {
                System.out.println("File could not be created for this reason: ");
                e.printStackTrace();
            }
        }
    }
}

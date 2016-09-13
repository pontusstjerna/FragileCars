package model.carcontrollers;

import model.GameObject;
import model.carcontrollers.util.BotPoint;
import model.cars.FragileCar;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pontu on 2016-04-08.
 */
public class BruteBot implements GameObject, DrawableBot{
    private FragileCar car;
    private List<BotPoint> walls;

    public enum Dir {LEFT, RIGHT, STRAIGHT}

    private Point spawnPoint;
    private int lastX, lastY;
    private int nPointsInRange = 0;
    private double moved = 0;
    private Random rand;


    private final int DEATH_THRESHOLD = 3;
    private final int WALL_THRESHOLD;
    private int STICK_LENGTH = 110;
    private final int GAS_THRESHOLD = 120;
    private final double MAX_SEARCH_LENGTH = 200;


    public BruteBot(FragileCar car, String trackName){
        this.car = car;

        walls = new ArrayList<>();
        spawnPoint = new Point(car.getX(), car.getY());
        lastX = car.getX();
        lastY = car.getY();

        WALL_THRESHOLD = Math.max(car.getImg().getWidth(), car.getImg().getHeight())/3;
        STICK_LENGTH = WALL_THRESHOLD*2;

        rand = new Random();

        //walls.add(new Point(200, 1108));
    }

    @Override
    public void update(double dTime){
        checkReset();

        if((STICK_LENGTH >= MAX_SEARCH_LENGTH || car.getAcceleration() < GAS_THRESHOLD)){
            car.accelerate();
        }else{
            car.brake();
        }

        STICK_LENGTH = rayTrace(car.getHeading());

        //adjustStick();

        avoid(dTime);

        //Should always be last
        updateCoords();
    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {

    }

    @Override
    public List<BotPoint> getBotPoints() {
        return walls;
    }


    @Override
    public int getWallThreshold() {
        return WALL_THRESHOLD;
    }

    @Override
    public int getStickLength() {
        return STICK_LENGTH;
    }

    @Override
    public FragileCar getCar() {
        return car;
    }

    @Override
    public int getStickX(){
        return (int)stickX();
    }

    @Override
    public int getStickY(){
        return (int)stickY();
    }


    private void checkReset(){
        //If car has been reset and not just standing on the spawn point
        if(car.getX() == spawnPoint.x && car.getY() == spawnPoint.y && (lastX != car.getX() || lastY != car.getY())){
            addWallPoint();

            //  System.out.println(car + " added wallpoint at (" + lastX + "," + lastY + ")");
        }
    }

    private void updateCoords(){
        lastX = car.getX();
        lastY = car.getY();
    }

    private void addWallPoint(){
        walls.add(new BotPoint(lastX, lastY, WALL_THRESHOLD));
    }


    private void avoid(double dTime){
       turn(bestTurn(car.getX(), car.getY(), car.getHeading()), dTime);
    }

    private void turn(Dir dir, double dTime){
        switch(dir){
            case LEFT:
                car.turnLeft(dTime);
                break;
            case RIGHT:
                car.turnRight(dTime);
                break;
            case STRAIGHT:
                break;
        }
    }

    private BotPoint getClosestWallPoint(double x, double y){
        if(walls.size() > 0){
            BotPoint closest = walls.get(0);
            for(BotPoint p : walls){
                if(p.distance(x, y) - p.getRadius() < closest.distance(x, y) - closest.getRadius()){
                    closest = p;
                }
            }
            if(closest.x != x || closest.y != y){
                return closest;
            }
        }
        return null;
    }

    private Dir getSide(double x, double y, Point wallPoint){

        double heading = getPI(getHeadingToPoint(wallPoint, x,y) - getPI(car.getHeading()));
        if(heading > Math.toRadians(160) || heading < Math.toRadians(-160)){
            return Dir.STRAIGHT;
        }else{
            if(heading > 0){
                return Dir.RIGHT;
            }else if(heading < 0){
                return Dir.LEFT;
            }else{
                return Dir.STRAIGHT;
            }
        }
    }

    private Dir getSide(double x, double y, Point wallPoint, double radians){

        double heading = getPI(getHeadingToPoint(wallPoint, x,y) - getPI(radians));
        if(heading > Math.toRadians(160) || heading < Math.toRadians(-160)){
            return Dir.STRAIGHT;
        }else{
            if(heading > 0){
                return Dir.RIGHT;
            }else if(heading < 0){
                return Dir.LEFT;
            }else{
                return Dir.STRAIGHT;
            }
        }
    }

    private Dir optimalTurn(double x, double y){

        Dir side = mostWalls(x,y, WALL_THRESHOLD*4, car.getHeading());

        if(side == Dir.LEFT){
            return Dir.RIGHT;
        }else if(side == Dir.RIGHT){
            return Dir.LEFT;
        }else{
            return Dir.STRAIGHT;
        }
    }

    private void adjustStick(){
        BotPoint closest = getClosestWallPoint(stickX(), stickY());
        STICK_LENGTH = closest != null ? (int)closest.distance(car.getX(), car.getY()) - 1 : 100;
    }

    private double stickX(){
        return car.getX() + Math.sin(car.getHeading())*STICK_LENGTH;
    }

    private double stickY(){
        return car.getY() - Math.cos(car.getHeading())*STICK_LENGTH;
    }

    private double stickX(double radians, double stickLength){
        return car.getX() + Math.sin(radians)*stickLength;
    }

    private double stickY(double radians, double stickLength){
        return car.getY() - Math.cos(radians)*stickLength;
    }

    private double checkStickX(double radians){
        //Need to make dynamic


        return car.getX() + Math.sin(radians)*STICK_LENGTH;
    }

    private double checkStickY(double radians){
        //Need to make dynamic
        double dynStickLength = WALL_THRESHOLD;
        final double MAX_SEARCH_LENGTH = 500;
        while(dynStickLength < MAX_SEARCH_LENGTH){

        }

        return car.getY() - Math.cos(radians)*STICK_LENGTH;
    }

    private Dir bestTurn(int x, int y, double heading){
        if(walls.size() > 0)
        {

            final double DIFF_THRESHOLD = 2;

            double sxR = stickX();
            double syR = stickY();

            double sxL = stickX();
            double syL = stickY();

            double radsLeft = 0;
            double radsRight = 0;
            double distLeft = 0;
            double distRight = 0;

            double stickRight = 0;
            double stickLeft = 0;

            try{

                //Try to turn right
                while(radsRight < Math.PI/2 && (getClosestWallPoint(sxR,syR).distance(sxR,syR) < WALL_THRESHOLD + 1)
                        || mostWalls(sxR, syR, WALL_THRESHOLD*2, radsRight) == Dir.RIGHT){
                    radsRight += Math.toRadians(1);
                    distRight = getClosestWallPoint(sxR,syR).distance(sxR,syR);

                    double dynStickLength = rayTrace(car.getHeading() + radsRight);
                    sxR = stickX(heading + radsRight, dynStickLength);
                    syR = stickY(heading + radsRight, dynStickLength);

                    if(dynStickLength >= stickRight){
                        stickRight = dynStickLength;
                    }


                }

                /*
                IDEA!!
                If the ray never reaches max length (500), then turn to the dir where the longest ray was reached!!!
                 */

                //Try to turn left
                while(radsLeft < Math.PI/2 && (getClosestWallPoint(sxL,syL).distance(sxL,syL) < WALL_THRESHOLD + 1)
                        || mostWalls(sxL, syL, WALL_THRESHOLD*2, radsLeft) == Dir.LEFT){
                    radsLeft += Math.toRadians(1);
                    distLeft = getClosestWallPoint(sxL,syL).distance(sxL,syL);

                    if(walls.size() > 3){
                        stickX();
                    }

                    double dynStickLength = rayTrace(car.getHeading() - radsLeft);
                    sxL = stickX(heading - radsLeft, dynStickLength);
                    syL = stickY(heading - radsLeft, dynStickLength);

                    if(dynStickLength >= stickLeft){
                        stickLeft = dynStickLength;
                    }
                }

                if((stickRight >= MAX_SEARCH_LENGTH && stickLeft >= MAX_SEARCH_LENGTH) ||
                (stickRight == 0 && stickLeft == 0)){
                    if(radsLeft == 0 && radsRight == 0){
                        return Dir.STRAIGHT;
                    }else if(Math.abs(radsRight - radsLeft) < Math.toRadians(DIFF_THRESHOLD)){
                        return optimalTurn(lastX,lastY);
                    }else if(radsLeft > radsRight){
                        return Dir.RIGHT;
                    }else{
                        return Dir.LEFT;
                    }
                }else{
                    return Dir.STRAIGHT;
                }
            }catch(NullPointerException e){
                System.out.println("NULL! " + e.toString());
            }
        }
        return Dir.STRAIGHT;
    }

    private Dir mostWalls(double x, double y, double distance, double heading){
        int right = 0;
        int left = 0;

        for(BotPoint p : walls){
            if(p.distance(x,y) < distance){
                //Check side
                if(getSide(x,y,p, heading) == Dir.RIGHT){
                    right++;
                }else if(getSide(x,y,p, heading) == Dir.LEFT){
                    left++;
                }
            }
        }

      //  System.out.println("nLRight: " + right + " nLeft: " + left);

        if(right > left){
            return Dir.RIGHT;
        }else if(left > right){
            return Dir.LEFT;
        }else{
            return Dir.STRAIGHT;
        }
    }

    private int rayTrace(double radians){
        int stickLength = 100;

        while(walls.size() > 0 && stickLength < MAX_SEARCH_LENGTH &&
                getClosestWallPoint(stickX(radians,stickLength),stickY(radians,stickLength)).
                        distance(stickX(radians,stickLength),stickY(radians,stickLength)) > WALL_THRESHOLD){
            stickLength++;
        }
        return stickLength;
    }

    private Dir randomDir(){
        if(rand.nextBoolean()){
            return Dir.LEFT;
        }else {
            return Dir.RIGHT;
        }
    }

    private double getPI(double angle) {
        angle = angle % Math.PI*2;
        if (angle >= Math.PI && angle > 0) {
            return angle - Math.PI*2;
        }else if(angle <= -Math.PI){
            return angle + Math.PI*2;
        }
        return angle;
    }

    private double getHeadingToPoint(Point p, double x, double y){
        return Math.atan2(p.y - y, p.x - x);
    }
}

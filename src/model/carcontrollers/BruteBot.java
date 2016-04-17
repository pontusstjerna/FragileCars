package model.carcontrollers;

import model.carcontrollers.util.WallPoint;
import model.cars.FragileCar;
import util.Vector2D;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pontu on 2016-04-08.
 */
public class BruteBot implements CarController, DrawableBot{
    private FragileCar car;
    private List<WallPoint> walls;

    public enum Dir {LEFT, RIGHT, STRAIGHT}

    private Point spawnPoint;
    private int lastX, lastY;
    private int nPointsInRange = 0;
    private double moved = 0;
    private Random rand;


    private final int DEATH_THRESHOLD = 3;
    private final int WALL_THRESHOLD;
    private int STICK_LENGTH = 150;
    private final int GAS_THRESHOLD = 120;


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

        if((nPointsInRange < DEATH_THRESHOLD || car.getAcceleration() < GAS_THRESHOLD)){
            car.accelerate();
        }else{
            car.brake();
        }

        //adjustStick();

        avoid(dTime);

        //Should always be last
        updateCoords();
    }

    @Override
    public List<WallPoint> getWallPoints() {
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
        walls.add(new WallPoint(lastX, lastY, WALL_THRESHOLD));
    }


    private void avoid(double dTime){
        if(walls.size() > 0){
            optimalTurn(stickX(), stickY());

            WallPoint closest = getClosestWallPoint(stickX(), stickY());
            if(closest.distance(stickX(),stickY()) < WALL_THRESHOLD){
                double radsLeft = 0;
                double radsRight = 0;

                while(radsRight < Math.PI &&
                        getClosestWallPoint(checkStickX(car.getHeading() + radsRight),
                                checkStickY(car.getHeading() + radsRight)).distance(checkStickX(car.getHeading() + radsRight),
                                checkStickY(car.getHeading() + radsRight)) < WALL_THRESHOLD*2){
                    radsRight += Math.toRadians(1);
                }
                while(radsLeft < Math.PI &&
                        getClosestWallPoint(checkStickX(car.getHeading() - radsLeft),
                                checkStickY(car.getHeading() - radsLeft)).distance(checkStickX(car.getHeading() - radsLeft),
                                checkStickY(car.getHeading() - radsLeft)) < WALL_THRESHOLD*2){
                    radsLeft += Math.toRadians(1);
                }

               // System.out.println("RadsLeft: " + radsLeft + " RadsRight: " + radsRight);

                final double MIN_DIFFERENCE = 5;

                if(Math.abs(radsLeft - radsRight) > Math.toRadians(MIN_DIFFERENCE)){
                    if(radsLeft > radsRight){
                        turn(Dir.RIGHT, dTime);
                    }else{
                        turn(Dir.LEFT, dTime);
                    }
                }else{
                    turn(optimalTurn(stickX(), stickY()), dTime);
                }
            }
        }
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

    private WallPoint getClosestWallPoint(double x, double y){
        if(walls.size() > 0){
            WallPoint closest = walls.get(0);
            for(WallPoint p : walls){
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
        int ax = car.getX();
        int ay = car.getY();

      /*  int bx = (int)x;
        int by = (int)y;

        Point c = wallPoint;


        double position = Math.signum(((bx - ax)*(c.y - ay) - (by - ay)*(c.x - ax)));
*/
        /*if(position > 0){
            System.out.println("Point to the right, turning left.");
        }else{
            System.out.println("Point to the left, turning right.");
        }*/
/*
        if(position > 0){
            return Dir.RIGHT;
        }else{
            return Dir.LEFT;
        }*/

        double heading = getPI(getHeadingToPoint(wallPoint, x,y) - getPI(car.getHeading()));
        if(heading > Math.toRadians(160) || heading < Math.toRadians(-160)){
            return Dir.STRAIGHT;
        }else{
            if(heading > 0){
                return Dir.RIGHT;
            }else{
                return Dir.LEFT;
            }
        }

    }

    private Dir optimalTurn(double x, double y){
        int nLeftPoints = 0;
        int nRightPoints = 0;

        Dir side = getSide(x,y,getClosestWallPoint(x,y));

        for(Point p : walls){
            if(p.distance(x,y) < WALL_THRESHOLD*2){
                if(getSide(x,y,p) == Dir.LEFT){
                    nLeftPoints++;
                }else{
                    nRightPoints++;
                }
            }
        }

        nPointsInRange = nLeftPoints + nRightPoints;
        final int TURN_THRESHOLD = 200;

        if(nLeftPoints - nRightPoints > TURN_THRESHOLD){
            return Dir.RIGHT;
        }else if(nRightPoints - nLeftPoints > TURN_THRESHOLD){
            return Dir.LEFT;
        }else{
            if(side == Dir.LEFT){
                return Dir.RIGHT;
            }else if(side == Dir.RIGHT){
                return Dir.LEFT;
            }else{
                return Dir.STRAIGHT;
            }
        }
    }

    private void adjustStick(){
        WallPoint closest = getClosestWallPoint(stickX(), stickY());
        STICK_LENGTH = closest != null ? (int)closest.distance(car.getX(), car.getY()) - 1 : 100;
    }

    private double stickX(){
        return car.getX() + Math.sin(car.getHeading())*STICK_LENGTH;
    }

    private double stickY(){
        return car.getY() - Math.cos(car.getHeading())*STICK_LENGTH;
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

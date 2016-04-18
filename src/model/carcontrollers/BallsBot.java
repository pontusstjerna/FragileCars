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
public class BallsBot implements CarController, DrawableBot{
    private FragileCar car;
    private List<WallPoint> walls;

    public enum Dir {LEFT, RIGHT, STRAIGHT}

    private Point spawnPoint;
    private int lastX, lastY;
    private int nPointsInRange = 0;
    private double moved = 0;
    private WallPoint currentWallPoint;


    private final int DEATH_THRESHOLD = 10;
    private final int WALL_THRESHOLD;
    private final int STICK_LENGTH = 100;
    private final int GAS_THRESHOLD = 100;


    public BallsBot(FragileCar car, String trackName){
        this.car = car;

        walls = new ArrayList<>();
        spawnPoint = new Point(car.getX(), car.getY());
        lastX = car.getX();
        lastY = car.getY();

        WALL_THRESHOLD = Math.max(car.getImg().getWidth(), car.getImg().getHeight());

        //walls.add(new Point(200, 1108));
    }

    @Override
    public void update(double dTime){
        checkReset();

        if((nPointsInRange < DEATH_THRESHOLD || car.getAcceleration() < GAS_THRESHOLD)){
            car.accelerate();
        }

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
        //If they overlap, basically
        if(false && currentWallPoint != null && merge(currentWallPoint, lastX, lastY)){

        }else{
            walls.add(new WallPoint(lastX, lastY, WALL_THRESHOLD));
        }

        //walls.add(new WallPoint(lastX, lastY, WALL_THRESHOLD));
    }

    private boolean merge(WallPoint closest, int x, int y){

        //If they overlap, basically
        if(closest != null && closest.distance(x, y) < closest.getRadius() + WALL_THRESHOLD) {
            int newX = x + (closest.x - x)/2;
            int newY = y + (closest.y - y)/2;
            walls.remove(closest);

            //Recursively merge all balls
            WallPoint merged = new WallPoint(newX, newY, WALL_THRESHOLD + closest.getRadius());
            merge(getClosestWallPoint(merged.x, merged.y), merged.x, merged.y);

            walls.add(merged);

            return true;
        }
        return false;
    }

    private void avoid(double dTime){

        if(walls.size() > 0){

            if(currentWallPoint == null || currentWallPoint.distance(stickX(), stickY()) > currentWallPoint.getRadius()){
                currentWallPoint = getClosestWallPoint(car.getX(), car.getY());
            }

            //Turn to the side where there are LEAST crashes
            if(currentWallPoint != null && currentWallPoint.distance(stickX(), stickY()) < currentWallPoint.getRadius()){
                turn(optimalTurn(car.getX(), car.getY()), dTime);
            }else{
             //   follow(dTime);

                /*
                NEW IDEA!!! MERGE WALLPOINTS SO THAT IF ONE IS SPAWNED CLOSE, MAKE ONE BIG SO THE TURNS WILL BE FUCKING
                ---->>>>DYNAMIC<<<<----
                 */
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


    private WallPoint getClosestWallPointBetween(double x, double y){
        WallPoint P = getClosestWallPoint(x,y);


        if(P != null){
            WallPoint Q = getClosestWallPoint(P.x, P.y);
            if(Q != null && P.distance(Q) < WALL_THRESHOLD){
                //DANIELS MAGISKA ALGORITM

                Vector2D PQ = new Vector2D(Q.x - P.x, Q.y - P.y);
                Vector2D Pr = new Vector2D(P.x - x, P.y - y);
                Vector2D v = Pr.sub(PQ.multiply((Pr.dot(PQ)/PQ.dot(PQ))));

                return new WallPoint((int)v.getX(), (int)v.getY(), WALL_THRESHOLD);
            }else{
                return P;
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

        Dir side = getSide(x,y,currentWallPoint);

        for(Point p : walls){
            if(p.distance(x,y) < WALL_THRESHOLD){
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


    private double stickX(){
        return car.getX() + Math.sin(car.getHeading())*STICK_LENGTH;
    }

    private double stickY(){
        return car.getY() - Math.cos(car.getHeading())*STICK_LENGTH;
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
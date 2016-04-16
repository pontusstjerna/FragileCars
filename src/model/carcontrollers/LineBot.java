package model.carcontrollers;

import model.carcontrollers.util.DrawableLineBot;
import model.carcontrollers.util.WallLine;
import model.carcontrollers.util.WallPoint;
import model.cars.FragileCar;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pontu on 2016-04-08.
 */
public class LineBot implements CarController, DrawableLineBot{
    private FragileCar car;
    private List<WallLine> walls;

    public enum Dir {LEFT, RIGHT, STRAIGHT}

    private Point spawnPoint;
    private int lastX, lastY;
    private double moved = 0;

    private final int WALL_THRESHOLD;
    private final int STICK_LENGTH = 100;
    private final int GAS_THRESHOLD = 100;


    public LineBot(FragileCar car, String trackName){
        this.car = car;

        walls = new ArrayList<>();
        spawnPoint = new Point(car.getX(), car.getY());
        lastX = car.getX();
        lastY = car.getY();

        WALL_THRESHOLD = Math.max(car.getImg().getWidth(), car.getImg().getHeight())/2;
    }

    @Override
    public void update(double dTime){
        checkReset();
        car.accelerate();
        avoid(dTime);

        //Should always be last
        updateCoords();
    }

    @Override
    public List<WallLine> getWallLines() {
        return walls;
    }

    @Override
    public List<WallPoint> getWallPoints() {
        return null;
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
            addWallLine();

            //  System.out.println(car + " added wallpoint at (" + lastX + "," + lastY + ")");
        }
    }

    private void updateCoords(){
        lastX = car.getX();
        lastY = car.getY();
    }

    private void addWallLine(){
        WallPoint closest = getClosestWallPoint(lastX, lastY);

        walls.add(new WallLine(new Point(lastX, lastY), WALL_THRESHOLD));
    }

    private boolean merge(WallPoint closest, int x, int y){

        //If they overlap, basically
        if(closest != null && closest.distance(x, y) < closest.getRadius() + WALL_THRESHOLD) {
            int newX = x + (closest.x - x)/2;
            int newY = y + (closest.y - y)/2;
           // wallPoints.remove(closest);

            //Recursively merge all balls
            WallPoint merged = new WallPoint(newX, newY, WALL_THRESHOLD + closest.getRadius());
            merge(getClosestWallPoint(merged.x, merged.y), merged.x, merged.y);

           // wallPoints.add(merged);

            return true;
        }
        return false;
    }

    private void avoid(double dTime){

       /* if(wallPoints.size() > 0){
            WallPoint closestPointStick = getClosestWallPoint(car.getX(), car.getY());

            //Turn to the side where there are LEAST crashes
            if(closestPointStick.distance(stickX(), stickY()) < closestPointStick.getRadius()){
                turn(optimalTurn(stickX(), stickY()), dTime);
            }else{
                //   follow(dTime);


            }
        }*/
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
        /*if(wallPoints.size() > 0){
            WallPoint closest = wallPoints.get(0);
            for(WallPoint p : wallPoints){
                if(p.distance(x, y) - p.getRadius() < closest.distance(x, y) - closest.getRadius()){
                    closest = p;
                }
            }
            return closest;
        }*/
        return null;
    }

    private Dir getSide(double x, double y, Point wallPoint){
        int ax = car.getX();
        int ay = car.getY();

        int bx = (int)x;
        int by = (int)y;

        Point c = wallPoint;


        double position = Math.signum(((bx - ax)*(c.y - ay) - (by - ay)*(c.x - ax)));

        /*if(position > 0){
            System.out.println("Point to the right, turning left.");
        }else{
            System.out.println("Point to the left, turning right.");
        }*/

        if(position > 0){
            return Dir.RIGHT;
        }else{
            return Dir.LEFT;
        }
    }

    private Dir optimalTurn(double x, double y){
        int nLeftPoints = 0;
        int nRightPoints = 0;

        Dir side = getSide(x,y,getClosestWallPoint(x,y));

      /*  for(Point p : walls){
            if(p.distance(x,y) < WALL_THRESHOLD){
                if(getSide(x,y,p) == Dir.LEFT){
                    nLeftPoints++;
                }else{
                    nRightPoints++;
                }
            }
        }*/

        final int TURN_THRESHOLD = 2;

        if(nLeftPoints - nRightPoints > TURN_THRESHOLD){
            return Dir.RIGHT;
        }else if(nRightPoints - nLeftPoints > TURN_THRESHOLD){
            return Dir.LEFT;
        }else{
            if(side == Dir.LEFT){
                return Dir.RIGHT;
            }else{
                return Dir.LEFT;
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

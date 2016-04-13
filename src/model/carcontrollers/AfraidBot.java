package model.carcontrollers;

import model.cars.FragileCar;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pontu on 2016-04-08.
 */
public class AfraidBot implements CarController, DrawableBot{
    private FragileCar car;
    private List<Point> walls;
    private List<Point> checkPoints;

    private enum Dir {LEFT, RIGHT, STRAIGHT}

    private Point spawnPoint;
    private int lastX, lastY;
    private int nPointsInRange = 0;
    private long startTime;
    private long longestTime = 0;
    private double moved = 0;
    private long longestMove = 0;

    private final int DEATH_THRESHOLD = 10;
    private final int WALL_THRESHOLD;
    private final int STICK_LENGTH = 50;
    private final int GAS_THRESHOLD = 100;
    private final int CHECKPOINT_DENSITY = 20;

    private Random rand;

    public AfraidBot(FragileCar car, String trackName, long countdown){
        this.car = car;

        walls = new ArrayList<>();
        checkPoints = new ArrayList<>();
        spawnPoint = new Point(car.getX(), car.getY());
        lastX = car.getX();
        lastY = car.getY();
        startTime = System.currentTimeMillis() + countdown;

        WALL_THRESHOLD = Math.max(car.getImg().getWidth(), car.getImg().getHeight());

        rand = new Random();
        //walls.add(new Point(200, 1108));
    }

    @Override
    public void update(double dTime){
        checkReset();

        if((nPointsInRange < DEATH_THRESHOLD || car.getAcceleration() < GAS_THRESHOLD)){
            car.accelerate();
        }
        avoid(dTime);

        addCheckPoint(dTime);

        //Should always be last
        updateCoords();
    }

    @Override
    public List<Point> getWallPoints() {
        return walls;
    }

    @Override
    public List<Point> getCheckPoints(){
        return checkPoints;
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
            walls.add(new Point(lastX, lastY));

          //  System.out.println(car + " added wallpoint at (" + lastX + "," + lastY + ")");
            moved = 0;
        }


    }

    private void updateCoords(){
        lastX = car.getX();
        lastY = car.getY();
    }

    private void avoid(double dTime){

        //Do same turns as went best so far
        /*if(checkPoints != null){
            for(CheckPoint p : checkPoints){
                if(p.getPoint().distance(car.getX(), car.getY()) < 1){
                    turnLeft = p.isTurnedLeft();
                    hasTurned = true;
                    //System.out.println(car + " turned at " + p.getPoint());
                }
            }
        }*/

        if(walls.size() > 0){
            Point closestPointStick = getClosestWallPoint(car.getX(), car.getY());

           /* //If stick is close to a point
            if(closestPointStick.distance(stickX(), stickY()) < WALL_THRESHOLD){
                turn(turnLeft(stickX(), stickY(), closestPointStick), dTime);
            }*/

            //Turn to the side where there are LEAST crashes
            if(closestPointStick.distance(stickX(), stickY()) < WALL_THRESHOLD){
                turn(optimalLeftTurn(stickX(), stickY()), dTime);
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

    private Point getClosestWallPoint(double x, double y){
        if(walls.size() > 0){
            Point closest = walls.get(0);
            for(Point p : walls){
                if(p.distance(stickX(), stickY()) < closest.distance(stickX(), stickY())){
                    closest = p;
                }
            }
            return closest;
        }
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

    private Dir optimalLeftTurn(double x, double y){
        int nLeftPoints = 0;
        int nRightPoints = 0;

        Dir side = getSide(x,y,getClosestWallPoint(x,y));

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

    private void addCheckPoint(double dTime){
        moved = moved + (car.getAcceleration()*dTime);

        if(moved > CHECKPOINT_DENSITY){
            checkPoints.add(new Point(car.getX(), car.getY()));
            moved = 0;
        }
    }
}

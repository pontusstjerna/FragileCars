package model.carcontrollers;

import model.cars.FragileCar;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pontu on 2016-04-08.
 */
public class AfraidBot implements CarController{
    private FragileCar car;
    private List<Point> walls;
    private List<TurnPoint> currentTurnPoints;
    private TurnPoint[] bestRoute;

    private Point spawnPoint;
    private int lastX, lastY;
    private boolean turnLeft = false;
    private boolean startTurnLeft;
    private boolean hasTurned = false;
    private long startTime;
    private long longestTime = 0;
    private long moved = 0;
    private long longestMove = 0;

    private final int DEATH_THRESHOLD = 5;
    private final int WALL_THRESHOLD = 200;
    private final int STICK_LENGTH = 10;

    private Random rand;

    public AfraidBot(FragileCar car, String trackName, long countdown){
        this.car = car;

        walls = new ArrayList<>();
        currentTurnPoints = new ArrayList<>();
        spawnPoint = new Point(car.getX(), car.getY());
        lastX = car.getX();
        lastY = car.getY();
        startTime = System.currentTimeMillis() + countdown;

        rand = new Random();
        startTurnLeft = rand.nextBoolean();
    }

    @Override
    public void update(double dTime){
        checkReset();

        if(System.currentTimeMillis() > startTime){
            car.accelerate();
        }
        avoid(dTime);
        moved += car.getAcceleration();

                //Should always be last
        updateCoords();
    }

    private void checkReset(){
        //If car has been reset and not just standing on the spawn point
        if(car.getX() == spawnPoint.x && car.getY() == spawnPoint.y && (lastX != car.getX() || lastY != car.getY())){
            walls.add(new Point(lastX, lastY));

            updateBestTrack();
          //  System.out.println(car + " added wallpoint at (" + lastX + "," + lastY + ")");
        }
    }

    private void updateCoords(){
        lastX = car.getX();
        lastY = car.getY();
    }

    private void avoid(double dTime){

        //Do same turns as went best so far
        /*if(bestRoute != null){
            for(TurnPoint p : bestRoute){
                if(p.getPoint().distance(car.getX(), car.getY()) < 1){
                    turnLeft = p.isTurnedLeft();
                    hasTurned = true;
                    //System.out.println(car + " turned at " + p.getPoint());
                }
            }
        }*/

        if(walls.size() > 0){
            Point closestPointStick = getClosestWallPointStick(car.getX(), car.getY());
            double stickX = car.getX() + Math.sin(car.getHeading())*STICK_LENGTH;
            double stickY = car.getY() + Math.cos(car.getHeading())*STICK_LENGTH;

            //If stick is close to a point
            if(closestPointStick.distance(stickX, stickY) < WALL_THRESHOLD){
                turn(turnLeft(stickX, stickY, closestPointStick), dTime);
            }
        }
    }

    private void turn(boolean turnLeft, double dTime){
        if(turnLeft){
            car.turnLeft(dTime);
        }else{
            car.turnRight(dTime);
        }
    }

    private Point getClosestWallPoint(){
        if(walls.size() > 0){
            Point closest = walls.get(0);
            for(Point p : walls){
                if(p.distance(car.getX(),car.getY()) < closest.distance(car.getX(), car.getY())){
                    closest = p;
                }
            }
            return closest;
        }
        return null;
    }

    private Point getClosestWallPointStick(double x, double y){
        if(walls.size() > 0){
            Point closest = walls.get(0);
            double stickX = x + Math.sin(car.getHeading())*STICK_LENGTH;
            double stickY = y + Math.cos(car.getHeading())*STICK_LENGTH;
            for(Point p : walls){
                if(p.distance(stickX, stickY) < closest.distance(stickX, stickY)){
                    closest = p;
                }
            }
            return closest;
        }
        return null;
    }

    private boolean turnLeft(double x, double y, Point wallPoint){
        double position = Math.signum((Math.sin(x)) * (wallPoint.y - y) - (Math.cos(y))
                * (wallPoint.x - x));

        return position < 0;
    }

    private void updateBestTrack(){
        if(moved > longestMove){
            longestMove = moved;
            if(currentTurnPoints.size() > 1){
                currentTurnPoints.remove(currentTurnPoints.size()-1);
                bestRoute = new TurnPoint[currentTurnPoints.size()];
                currentTurnPoints.toArray(bestRoute);
            }
        }

        /*if(timesDied > 20){
            bestRoute = null;
        }*/

        //System.out.println(car + " longest move: " + longestMove);

        currentTurnPoints.clear();
        moved = 0;
        turnLeft = startTurnLeft;
    }
}

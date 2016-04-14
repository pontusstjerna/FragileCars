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

    public enum Dir {LEFT, RIGHT, STRAIGHT}

    private Point spawnPoint;
    private int lastX, lastY;
    private int nPointsInRange = 0;
    private double moved = 0;
    private Dir currentDir = Dir.STRAIGHT;

    private int cpIndex = 1;

    private long startTime;

    private final int DEATH_THRESHOLD = 10;
    private final int WALL_THRESHOLD;
    private final int STICK_LENGTH = 100;
    private final int GAS_THRESHOLD = 100;
    private final int CHECKPOINT_DENSITY = 300;
    private final int MIN_CHECKPOINT_DISTANCE = 300;

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

        //follow(dTime);
        avoid(dTime);
       // checkCheckpoint();
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
            removeCheckpoints();
        }
    }

    private void updateCoords(){
        lastX = car.getX();
        lastY = car.getY();
    }

    private void avoid(double dTime){

        if(walls.size() > 0){
            Point closestPointStick = getClosestWallPoint(car.getX(), car.getY());

            //Turn to the side where there are LEAST crashes
            if(closestPointStick.distance(stickX(), stickY()) < WALL_THRESHOLD){
                turn(optimalTurn(stickX(), stickY()), dTime);
            }else{
             //   follow(dTime);

                /*
                NEW IDEA!!! MERGE WALLPOINTS SO THAT IF ONE IS SPAWNED CLOSE, MAKE ONE BIG SO THE TURNS WILL BE FUCKING
                ---->>>>DYNAMIC<<<<----
                 */
            }
        }
    }

    private void follow(double dTime){
        if(checkPoints.size() > 0 && getClosestCheckPoint(stickX(), stickY()) != null){
            Point closest = getClosestCheckPoint(stickX(), stickY());

            double headingToCP = getHeadingToPoint(closest, stickX(), stickY());
            double toTurn = getPI(headingToCP - getPI(car.getHeading()));
            //double toTurn = headingToCP - car.getHeading();


            final double turnThreshold = 0;


            if(toTurn > turnThreshold){
                turn(Dir.RIGHT, dTime);
            }else if(toTurn < -turnThreshold){
                turn(Dir.LEFT, dTime);
            }else{
                turn(Dir.STRAIGHT, dTime);
            }
        }
    }

    private void checkCheckpoint(){
        if(cpIndex < checkPoints.size())
        {
            double headingToCP = Math.atan2(checkPoints.get(cpIndex).getY() - car.getY(), checkPoints.get(cpIndex).getX() - car.getX());

            //If close enough and in front of check point
            if(checkPoints.get(cpIndex).distance(car.getX(), car.getY()) < MIN_CHECKPOINT_DISTANCE &&
                    (headingToCP > Math.PI/4) && headingToCP < Math.PI*3/4){
                System.out.println("Checked with " + checkPoints.get(cpIndex).distance(car.getX(), car.getY()));
                cpIndex++;
            }
        }
    }

    private void turn(Dir dir, double dTime){
        switch(dir){
            case LEFT:
                car.turnLeft(dTime);
                currentDir = Dir.LEFT;
                break;
            case RIGHT:
                car.turnRight(dTime);
                currentDir = Dir.RIGHT;
                break;
            case STRAIGHT:
                currentDir = Dir.STRAIGHT;
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

    private Point getClosestCheckPoint(double x, double y){
        if(checkPoints.size() > 0){
            Point closest = checkPoints.get(0);
            for(Point p : checkPoints){
                double headingToCP = getHeadingToPoint(p, x, y);
                if(p.distance(x, y) < closest.distance(x,y) &&
                        headingToCP < Math.PI/4 && headingToCP < -Math.PI/4){
                    closest = p;
                }
            }
            if(getHeadingToPoint(closest, x,y) < Math.PI/4 && getHeadingToPoint(closest, x,y) < -Math.PI/4 ) {
                return closest;
            }
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

    private Dir optimalTurn(double x, double y){
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

        //System.out.println("Moved: " + moved + " cpIndex: " + cpIndex + " Cps: " + checkPoints.size());

        //Only add checkpoints with a certain density and only if you have gone through all the old ones
        if(moved > CHECKPOINT_DENSITY && getClosestCheckPoint(stickX(), stickY()) == null){
            checkPoints.add(new Point(car.getX(), car.getY()));
            //CHECK HERE IF THE CLOSEST CHECKPOINT IS CLOSE, OTHERWISE, CREATE A NEW ONE!!
            moved = 0;
        }
    }

    private void removeCheckpoints(){
        final int CP_THRESHOLD = 2;
        int cpSize = checkPoints.size();

        //System.out.println("CPSize: " + cpSize);

        if(checkPoints.size() >= CP_THRESHOLD){
            for(int i = cpSize - 1; i > cpSize - CP_THRESHOLD; i--){
                checkPoints.remove(i);
            }
        }else{
            for(int i = cpSize - 1; i > 0; i--){
                checkPoints.remove(i);
            }
        }

        cpIndex = 1;
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

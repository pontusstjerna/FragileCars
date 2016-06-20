package model.carcontrollers;

import model.carcontrollers.util.BotPoint;
import model.cars.FragileCar;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pontu on 2016-04-08.
 */
public class CheckBot implements CarController, DrawableBot{

    private FragileCar car;
    private List<BotPoint> checkPoints = new ArrayList<>();
    private Random rand;
    private Point spawnPoint;

    private final int STICK_LENGTH = 100;

    private double distance = 0;

    public CheckBot(FragileCar car, String trackName){
        this.car = car;
        rand = new Random();
        spawnPoint = new Point(car.getX(), car.getY());

        checkPoints.add(new BotPoint(140, 800));
    }

    @Override
    public void update(double deltaTime) {
        if(isInFront(car, checkPoints.get(0))){
            car.accelerate();
        }
        search(deltaTime);
        checkReset();
        distance += car.getAcceleration()*deltaTime;
    }

    @Override
    public List<BotPoint> getBotPoints() {
        return checkPoints;
    }

    @Override
    public int getWallThreshold() {
        return 0;
    }

    @Override
    public int getStickLength() {
        return 0;
    }

    @Override
    public int getStickX() {
        return (int)stickX();
    }

    @Override
    public int getStickY() {
        return (int)stickY();
    }

    @Override
    public FragileCar getCar() {
        return car;
    }

    //Follow the previous best path
    private void follow(double dTime){

    }

    //Tries to find a valid way
    private boolean turnLeft = false;
    private int turnTimer = 0;
    private int turnTime = 0;
    private void search(double dTime){

        //Minimum time in a turn (frames)
        final int minTimeTurn = (int)(1000*dTime);

        //Maximum time in a turn
        final int maxTimeTurn = (int)(10000*dTime);

        if(turnTimer >= turnTime){
            turnTimer = 0;
            turnTime = rand.nextInt(maxTimeTurn) + minTimeTurn;
            turnLeft = rand.nextBoolean();
        }else{
            if(turnLeft){
                car.turnLeft(dTime);
            }else{
                car.turnRight(dTime);
            }
            turnTimer++;
        }

       // addCheckPoints();
    }

    //If searching, regularly add new checkpoints as long as not dying
    private void addCheckPoints(){
        final int spawn_freq = 20;

        if((int)distance % spawn_freq == 0){
            checkPoints.add(new BotPoint(getStickX(), getStickY()));
        }
    }

    //Is the point in front of the stick?
    private boolean isInFront(FragileCar car, Point point){

        double bearing = getBearing(car, point);

        System.out.println(
                "Bearing: " + Math.toDegrees(bearing) +
        " Heading: " + Math.toDegrees(getPI(car.getHeading())));

        return bearing < Math.PI/2 && bearing > -Math.PI/2;
    }

    private void checkReset(){
        if(spawnPoint.distance(car.getX(), car.getY()) < 1 && distance > 10){
            reset();
        }
    }

    private void reset(){
        //System.out.println("Distance traveled before dying: " + distance);
        distance = 0;
    }

    private double stickX(){
        return car.getX() + Math.sin(car.getHeading())*STICK_LENGTH;
    }

    private double stickY(){
        return car.getY() - Math.cos(car.getHeading())*STICK_LENGTH;
    }

    private double getBearing(FragileCar car, Point point){
        return getPI(getHeadingToPoint(point, car.getX(), car.getY()) - (Math.atan2(point.y, point.x) - getPI(car.getHeading())));

        /*
        //double deltaAngle = korven.getRadarHeading() - korven.getGunHeading();
        double distance = e.getDistance();
        double enemyVel = e.getVelocity();
        firePower = 3 - 3 * distance / korven.getMaxDistance();
        double bulletSpeed = 20 - 3 * firePower;

        toEnemy = Vector2D.getHeadingVector(korven.getRadarHeadingRadians(), e.getDistance(), 1);
        enemyPath = Vector2D.getHeadingVector(e.getHeadingRadians(), (e.getDistance()*enemyVel/bulletSpeed) +
                distance*0.18*Math.abs(e.getVelocity())/8, 1);
        toHitPoint = Vector2D.add(toEnemy, enemyPath);

        deltaAngle = toHitPoint.getHeading() - korven.getGunHeading();

        //additional = e.getDistance()*0.024*Math.signum(deltaAngle);

        if(deltaAngle < 180 && deltaAngle > -180){
            korven.setTurnGunRight(deltaAngle);
        }else if(deltaAngle > 180){
            korven.setTurnGunRight(360 - deltaAngle);
        }else{
            korven.setTurnGunRight(360 + deltaAngle);
        }
         */
    }

    private double getHeadingToPoint(Point p, double x, double y){
        return Math.atan2(p.y - y, p.x - x);
    }

    private double getPI(double angle) {
        angle = angle % (Math.PI*2);
        if (angle >= Math.PI && angle > 0) {
            return angle - Math.PI*2;
        }else if(angle <= -Math.PI){
            return angle + Math.PI*2;
        }
        return angle;
    }
}

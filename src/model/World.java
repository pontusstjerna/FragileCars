package model;

import model.cars.Car;
import model.cars.DrawableCar;
import model.cars.FragileCar;
import util.CfgParser;
import util.DirectionalRect;
import util.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Pontus on 2016-03-04.
 */
public class World implements Racetrack{
    public static final int WORLD_WIDTH = 1600;
    public static final int WORLD_HEIGHT = 1200;

    private BufferedImage[] images;

    private FragileCar[] players;
    private FragileCar[] bots;
    private FragileCar[] cars;
    private DrawableCar[] drawables;
    private boolean[] hasPassed;

    private DirectionalRect goal;

    private int nPlayers;
    private int laps;

    private long startTime;
    private long time;
    private long countdown;
    private boolean finished = false;

    public World(){
        loadData();
        createWorld();
    }

    public void update(double deltaTime) {
        for(FragileCar c : players){
            c.update(deltaTime);
        }
        releaseCars();
        checkCollisions();
        checkLaps();
    }

    public FragileCar[] getCars(){
        FragileCar[] cars = new FragileCar[players.length + bots.length];

        for(int i = 0; i < players.length; i++){
            cars[i] = players[i];
        }
        for(int i = 0; i < bots.length; i++){
            cars[i+players.length] = bots[i];
        }

        return cars;
    }

    @Override
    public DrawableCar[] getDrawables(){
        return drawables;
    }

    @Override
    public boolean getFinished(){
        return finished;
    }

    public FragileCar[] getPlayers(){
        return players;
    }

    @Override
    public BufferedImage getBackground(){
        return images[0];
    }

    @Override
    public BufferedImage getForeground(){
        return images[1];
    }

    @Override
    public long getTime(){
        if(!finished){
            time = System.currentTimeMillis() - startTime - countdown;
        }
        return time;
    }

    private void createWorld(){
        initImages();
        findGoalLine();
        createCars(nPlayers);
        startTime = System.currentTimeMillis();
        System.out.println("World created with " + nPlayers + " players and " + bots.length + " bots.");
    }

    private void loadData(){
        CfgParser cfg = new CfgParser("src\\model\\data\\config.txt");

        nPlayers = cfg.readInt("nPlayers");
        laps = cfg.readInt("laps");
        countdown = cfg.readLong("countdown");
    }

    private void findGoalLine(){
        /*
        Do like a spawn-area with 4 pixels with certain colors for back and front. Spawn cars in the area/square to start.
        New lap by coming from behind, going through and leaving.
         */

        //Search for the marking pixels that are RED.
        int goalX = 0;
        int goalY = 0;
        for(int x = 0; x < images[0].getWidth(); x++){
            for(int y = 0; y < images[0].getHeight(); y++){
                //System.out.println(new Color(images[0].getRGB(x,y)) + "(" + x + "," + y + ")");
                if(images[0].getRGB(x,y) == Color.RED.getRGB() && goalX == 0 && goalY == 0){
                    goalX = x;
                    goalY = y;
                }else if(images[0].getRGB(x,y) == Color.RED.getRGB()){

                    //Create the rectangle with correct alignment.
                    goal = new DirectionalRect(goalX, goalY, Math.max(Math.abs(goalX - x), Math.abs(goalY - y)), 200,
                            Math.abs(goalX - x) > Math.abs(goalY - y) ? DirectionalRect.Direction.UP : DirectionalRect.Direction.LEFT);
                }
            }
        }

        System.out.println("Goal created: ");
        System.out.println(goal);
    }

    private void initImages(){
        images = new BufferedImage[2];

        images[0] = ImageHandler.loadImage("background");
        images[1] = ImageHandler.loadImage("foreground");
    }

    private void createCars(int nPlayers){
        players = new FragileCar[nPlayers];
        bots = new FragileCar[4 - nPlayers];
        cars = new FragileCar[players.length + bots.length];
        drawables = new DrawableCar[cars.length];

        for(int i = 0; i < players.length; i++){
            Car car = new Car(Car.Cars.values()[i], 400, 100*i + 800, Math.PI*3/2);
            players[i] = car;
            drawables[i] = car;
        }

        for(int i = 0; i < bots.length; i++){
            Car car = new Car(Car.Cars.values()[nPlayers + i], 400, 100*(i+nPlayers) + 800, Math.PI*3/2);
            bots[i] = car;
            drawables[i + players.length] = car;
        }

        for(int i = 0; i < players.length; i++){
            cars[i] = players[i];
        }
        for(int i = 0; i < bots.length; i++){
            cars[i+players.length] = bots[i];
        }

        hasPassed = new boolean[cars.length];
    }

    private void checkCollisions(){
        for(FragileCar car : getCars()){
            for(int x = 0; x < car.getImg().getWidth(); x++){
                for(int y = 0; y < car.getImg().getHeight(); y++){
                    if(car.getImg().getRGB(x,y) != 0){
                        if(images[1].getRGB(car.getX() + x, car.getY() + y) != 0){
                            car.reset();
                        }
                    }
                }
            }
        }
    }

    private int places = 0;
    private void checkLaps(){
        boolean allFinished = false;
        for(int i = 0; i < cars.length; i++){
            if(goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.FRONT && hasPassed[i]){
                cars[i].newLap();

                //Finished?
                if(cars[i].getLaps() == laps){
                    places++;
                    cars[i].finish(getTime(), places);
                    cars[i].turnOff(true);

                    allFinished = true;
                }else{
                    allFinished = false;
                    System.out.println(cars[i]);
                }
            }
            hasPassed[i] = goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.BACK;
        }
        finished = allFinished;
    }

    private void releaseCars(){
        if(getTime()/10 == 0){
            for(FragileCar car : cars){
                car.setLocked(false);
            }
        }
    }

    @Override
    public String toString(){
        return "World with "  + players.length + " players and " + bots.length + " bots.";
    }
}

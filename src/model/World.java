package model;

import model.carcontrollers.*;
import model.cars.Car;
import model.cars.DrawableCar;
import model.cars.FragileCar;
import util.CfgParser;
import util.DirectionalRect;
import util.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Pontus on 2016-03-04.
 */
public class World implements Racetrack{
    public static final int WORLD_WIDTH = 1600;
    public static final int WORLD_HEIGHT = 1200;

    private String track;
    private BufferedImage[] images;

    private FragileCar[] players;
    private FragileCar[] bots;
    private FragileCar[] cars;
    //private GameObject[] controllers;
    private ArrayList<GameObject> objects;
    private DrawableBot[] drawableBots;
    private DrawableCar[] drawables;
    private boolean[] passedBack;
    private boolean[] passedFront;

    private Class botClass;

    private DirectionalRect goal;

    private int nCars;
    private int nPlayers;
    private int laps;

    private long startTime;
    private long time;
    private long countdown;
    private double deltaTime;
    private boolean finished = false;
    private double friction;

    public World(){
        loadData();
        createWorld();
    }

    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
        getTime();
        updateCars(deltaTime);
        releaseCars();
        checkCollisions();
        updateObjects(deltaTime);
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
    public DrawableCar[] getDrawableCars(){
        return drawables;
    }

    //@Override
    //public GameObject[] getObjects(){
   //     return controllers;
  //  }

    public ArrayList<GameObject> getObjects() {
        return objects;
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

    private double timePassed = 0;
    private int fps = 0;

    @Override
    public int getFPS(){
        final double fpsUpdateInterval = 0.5;

        timePassed += deltaTime;
        if(timePassed > fpsUpdateInterval){
            fps = (int)(1/deltaTime);
            timePassed = 0;
        }
        return fps;
    }

    @Override
    public int getMaxLaps(){
        return laps;
    }

    private void createWorld(){
        initImages();
        findGoalLine();
        createCars();
        createMiscGameObjects();
        startTime = System.currentTimeMillis();
        System.out.println("World created with " + nPlayers + " players and " + bots.length + " bot(s).");
    }

    private void loadData(){
        CfgParser cfg = new CfgParser(CfgParser.STD_PATH);

        track = cfg.readString("trackName");
        nCars = cfg.readInt("nCars");
        nPlayers = cfg.readInt("nPlayers");
        laps = cfg.readInt("laps");
        countdown = cfg.readLong("countdown");
        friction = cfg.readDouble("friction");
        try{
            botClass = cfg.readClass("botClass");
        }catch(ClassNotFoundException e){
            System.out.println("No such class found!");
            e.printStackTrace();
        }
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
                    goal = new DirectionalRect(goalX, goalY, Math.max(Math.abs(goalX - x), Math.abs(goalY - y)), 400,
                            Math.abs(goalX - x) > Math.abs(goalY - y) ? DirectionalRect.Direction.UP : DirectionalRect.Direction.LEFT);
                }
            }
        }

        System.out.println("Goal created: ");
        System.out.println(goal);
    }

    private void initImages(){
        images = new BufferedImage[2];

        images[0] = ImageHandler.loadImage(track + "_bg");
        images[1] = ImageHandler.loadImage(track + "_fg");
    }

    private void createCars(){
        players = new FragileCar[Math.min(nCars, nPlayers)];
        bots = new FragileCar[Math.max(nCars - nPlayers, 0)];
        cars = new FragileCar[players.length + bots.length];
        drawables = new DrawableCar[cars.length];
        drawableBots = new DrawableBot[bots.length];

        //Create player cars and add to drawables and cars
        for(int i = 0; i < players.length; i++){
            if(goal.getDir() == DirectionalRect.Direction.LEFT){
                spawnCarsLeft(i, false);
            }else{
                spawnCarsUp(i, false);
            }
        }

        //Create bots and add to drawables and cars
        for(int i = 0; i < bots.length; i++){
            if(goal.getDir() == DirectionalRect.Direction.LEFT){
                spawnCarsLeft(i, true);
            }else{
                spawnCarsUp(i, true);
            }
        }

        passedBack = new boolean[cars.length];
        passedFront = new boolean[cars.length];
    }

    private void createMiscGameObjects(){
        objects = new ArrayList<>();

        for(int i = 0; i < cars.length; i++){
            for(int j = 0; j < drawables[0].getGameObjects().length; j++){
                objects.add(drawables[i].getGameObjects()[j]);
            }
        }

        //Create car controllers for bots
        try{
            for(int i = 0; i < bots.length; i ++){
                //GameObject bot = new CheckBot(bots[i], "1");

                //My ugly hack for using custom bot-classes for other programmers to implement
                GameObject bot = (GameObject) botClass.getDeclaredConstructor
                        (new Class[]{FragileCar.class, String.class}).newInstance(bots[i], track);

                objects.add(bot);
            }
        }catch(Exception e){
            System.out.println("A lot of things went wrong when loading your custom bot-class. Sorry... :/" +
                    "Your code probably has a lot of bugs.");
            e.printStackTrace();
        }
    }

    private void spawnCarsLeft(int carIndex, boolean isBot){
        if(!isBot){
            Car car = new Car(Car.Cars.values()[carIndex], goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).x + 100,
                    goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).y + 100*(carIndex) + 100, Math.PI*3/2, friction);
            players[carIndex] = car;
            cars[carIndex] = players[carIndex];
            drawables[carIndex] = car;
        }else{
            Car car = new Car(Car.Cars.values()[nPlayers + carIndex],
                    goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).x + 100,
                    100*(carIndex+nPlayers) + goal.getCorner(DirectionalRect.Corner.FRONT_RIGHT).y + 100, Math.PI*3/2, friction);
            bots[carIndex] = car;
            cars[carIndex+players.length] = bots[carIndex];
            drawables[carIndex + players.length] = car;
        }
    }

    private void spawnCarsUp(int carIndex, boolean isBot){
        if(!isBot){
            Car car = new Car(Car.Cars.values()[carIndex],
                    goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).x + 100 + 100*(carIndex),
                    goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).y - 100, 0, friction);
            players[carIndex] = car;
            cars[carIndex] = players[carIndex];
            drawables[carIndex] = car;
        }else{
            Car car = new Car(Car.Cars.values()[nPlayers + carIndex],
                    goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).x + 100 + 100*(carIndex + nPlayers),
                    goal.getCorner(DirectionalRect.Corner.FRONT_LEFT).y - 100, 0, friction);
            bots[carIndex] = car;
            cars[carIndex+players.length] = bots[carIndex];
            drawables[carIndex + players.length] = car;
        }
    }

    private void checkCollisions(){
        for(FragileCar car : getCars()){
            for(int x = 0; x < car.getWidth(); x++){
                for(int y = 0; y < car.getHeight(); y++){
                    try{
                        if(car.getImgs()[car.getFrame()].getRGB(x,y) != 0){
                            if(images[1].getRGB(car.getX() + x,
                                                car.getY() + y) != 0){
                                car.reset();
                            }
                        }
                    }catch(ArrayIndexOutOfBoundsException e){
                        car.reset();
                    }

                }
            }
        }
    }

    private int places = 0;
    private void checkLaps(){
        boolean allFinished = false;
        for(int i = 0; i < cars.length; i++){
            if(goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.FRONT && passedBack[i]) {
                cars[i].newLap();

                //Finished?
                if (cars[i].getLaps() == laps) {
                    places++;
                    cars[i].finish(getTime(), places);
                    cars[i].turnOff(true);

                    allFinished = true;
                } else {
                    allFinished = false;
                    System.out.println(cars[i]);
                }
            }else if(goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.BACK && passedFront[i]){
                cars[i].reset();
            }

            passedBack[i] = (goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.BACK);
            passedFront[i] = (goal.backOrFront(cars[i].getX(), cars[i].getY()) == DirectionalRect.Side.FRONT);
        }
        finished = allFinished;
    }

    private boolean released = false;
    private void releaseCars(){
        if(!released && time > 0){
            for(FragileCar car : cars){
                car.setLocked(false);
            }
            released = true;
        }
    }

    private void updateCars(double dTime){
        for(FragileCar c : cars){
            c.update(dTime);
        }
    }

    private void updateObjects(double dTime){
        for(GameObject obj : objects){
            if(obj != null){
                obj.update(dTime);
            }
        }
    }

    @Override
    public String toString(){
        return "World with "  + players.length + " players and " + bots.length + " bots.";
    }
}

package model;

import util.CfgParser;

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

    private int nPlayers;
    private int laps;

    private long startTime;

    public World(){
        loadData();
        createWorld();
    }

    public void update(double deltaTime) {
        for(FragileCar c : players){
            c.update(deltaTime);
        }

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

    public FragileCar[] getPlayers(){
        return players;
    }

    public BufferedImage[] getImages(){
        return images;
    }

    @Override
    public long getTime(){
        //Includes 3 seconds start time
        return System.currentTimeMillis() - startTime - 3000;
    }

    private void createWorld(){
        initImages();
        createCars(nPlayers);
        startTime = System.currentTimeMillis();
        System.out.println("World created with " + nPlayers + " players and " + bots.length + " bots.");
    }

    private void loadData(){
        CfgParser cfg = new CfgParser("src\\model\\data\\config.txt");

        nPlayers = cfg.readInt("nPlayers");
    }

    private void findGoalLine(){
        /*
        Do like a spawn-area with 4 pixels with certain colors for back and front. Spawn cars in the area/square to start.
        New lap by coming from behind, going through and leaving.
         */

    }

    private void initImages(){
        images = new BufferedImage[2];

        images[0] = ImageHandler.loadImage("background");
        images[1] = ImageHandler.loadImage("foreground");
    }

    private void createCars(int nPlayers){
        players = new FragileCar[nPlayers];
        bots = new FragileCar[4 - nPlayers];

        for(int i = 0; i < players.length; i++){
            players[i] = new Car(Car.Cars.values()[i], 400, 100*i + 800, Math.PI*3/2);
        }

        for(int i = 0; i < bots.length; i++){
            bots[i] = new Car(Car.Cars.values()[nPlayers + i], 400, 100*(i+nPlayers) + 800, Math.PI*3/2);
        }
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

    private void checkLaps(){

    }

    @Override
    public String toString(){
        return "World with "  + players.length + " players and " + bots.length + " bots.";
    }
}

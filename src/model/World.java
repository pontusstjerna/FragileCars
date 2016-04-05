package model;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by Pontus on 2016-03-04.
 */
public class World{
    public static final int WORLD_WIDTH = 1600;
    public static final int WORLD_HEIGHT = 1200;

    private BufferedImage[] images;

    private FragileCar[] players;
    private FragileCar[] bots;

    private Random rand;

    public World(int nPlayers){
        rand = new Random();
        createWorld(nPlayers);
    }

    public void update(double deltaTime) {
        for(FragileCar c : players){
            c.update(deltaTime);
        }

        checkCollisions();
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

    private void createWorld(int nPlayers){
        initImages();
        createCars(nPlayers);
        System.out.println("World created with " + nPlayers + " players and " + bots.length + " bots.");
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
            players[i] = new Car(Car.Cars.values()[i], 100*i + 70, 1050);
        }

        for(int i = 0; i < bots.length; i++){
            bots[i] = new Car(Car.Cars.values()[nPlayers + i], 100*(i+nPlayers) + 70, 1050);
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

    @Override
    public String toString(){
        return "World with "  + players.length + " players and " + bots.length + " bots.";
    }
}

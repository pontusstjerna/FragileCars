package controller;

import model.World;
import view.MainWindow;
import view.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainController implements ActionListener {
    private MainWindow frame;
    private View view;
    private Timer timer;
    private World world;
    private PlayerController playerController;
    private MenuController menu;

    private double tempTime;
    private double deltaTime;
    private final int DELAY = 5;

    public MainController(){

    }

    public void init(){
        //Use accelerated graphics
        System.setProperty("sun.java2d.opengl", "true");
        initView();
        startGame();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        setDeltaTime();

        playerController.update(getDeltaTime());
        world.update(getDeltaTime());
        frame.repaint();

        //System.out.println("Fps: " + (1/getDeltaTime()));
        //System.out.println("Time: " + world.getTime());
    }

    public double getDeltaTime(){
        return deltaTime;
    }

    private PlayerController initPlayerControls(World world){
        return new PlayerController(world.getPlayers());
    }

    private void initView(){
        frame = new MainWindow("Fragile Cars");
        frame.init();
    }

    /*private void initView(){
        view = new View("FragileCars", world, playerController);
    }*/

    private void startGame(){
        world = initWorld();
        playerController = initPlayerControls(world);
        frame.startGame(world, playerController);

        initTimer();
        timer.start();
        System.out.println("Game started!");
        System.out.println("------------------------------");
    }

    private void initTimer(){
        timer = new Timer(DELAY, this);
    }

    private World initWorld(){
        return new World();
    }

    private void setDeltaTime(){
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - tempTime)/1000;
        tempTime = currentTime;
    }
}

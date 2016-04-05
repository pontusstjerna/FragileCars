package controller;

import model.World;
import view.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainController implements ActionListener {
    private MainWindow frame;
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
        menu = new MenuController();
        initView();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        setDeltaTime();

        if(menu == null){
            playerController.update(getDeltaTime());
            world.update(getDeltaTime());
            frame.repaint();
            //System.out.println("Fps: " + (1/getDeltaTime()));
        }else{
            menu.actionPerformed(e);
            if(menu.done()){
                startGame();
            }
        }
    }

    public double getDeltaTime(){
        return deltaTime;
    }

    private PlayerController initPlayerControls(World world){
        return new PlayerController(world.getPlayers());
    }

    private void initView(){
        frame = new MainWindow("Fragile Cars");
        frame.init(this);
    }

    private void startGame(){
        world = initWorld();
        playerController = initPlayerControls(world);
        frame.startGame(world.getCars(), world.getImages(), menu.getShowVectors(), playerController);

        menu = null;
        initTimer();
        timer.start();
        System.out.println("Game started!");
        System.out.println("------------------------------");
    }

    private void initTimer(){
        timer = new Timer(DELAY, this);
    }

    private World initWorld(){
        return new World(menu.getnPlayers());
    }

    private void setDeltaTime(){
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - tempTime)/1000;
        tempTime = currentTime;
    }
}

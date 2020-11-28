package se.nocroft.controller;

import se.nocroft.model.World;
import se.nocroft.model.drivers.*;
import se.nocroft.model.cars.Car;
import se.nocroft.model.cars.CarSetup;
import se.nocroft.view.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Note to self: PostNord 9108

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainController implements ActionListener, GameController {
    private MainWindow frame;
    private Timer timer;
    private World world;
    private PlayerController playerController;
    private boolean finished = false;

    private double tempTime;
    private double deltaTime;
    private final int DELAY = 5;

    public void init() {
        // Use accelerated graphics
        System.setProperty("sun.java2d.opengl", "true");

        world = new World();
        initView();
        frame.showMenu();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // If finished, wait 5 sec and exit game
        if (!finished && world.getFinished()) {
            Timer t = new Timer(5000, (ae) -> closeGame());
            t.setRepeats(false);
            t.start();
            finished = true;
        }
        setDeltaTime();

        playerController.update(getDeltaTime());
        world.update(getDeltaTime());
        frame.repaint();

        // System.out.println("Fps: " + (1/getDeltaTime()));
        // System.out.println("Time: " + world.getTime());
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    @Override
    public void startGame(CarSetup[] carSetups) {
        world.start(carSetups);
        playerController = initPlayerControls(world);
        frame.startGame(world, playerController);

        initTimer();
        timer.start();
        System.out.println("Game started!");
        System.out.println("------------------------------");
    }

    private PlayerController initPlayerControls(World world) {
        return new PlayerController(world.getPlayers());
    }

    private void initView() {
        frame = new MainWindow("Fragile Cars");
        frame.init(world.getBackground().getWidth(), world.getBackground().getHeight(), this);
    }

    private void closeGame() {
        timer.stop();
        frame.setVisible(false);
        frame.dispose();
        frame = null;
        world = null;
        playerController = null;
    }

    private void initTimer() {
        timer = new Timer(DELAY, this);
    }

    private void setDeltaTime() {
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - tempTime) / 1000;
        tempTime = currentTime;
    }
}

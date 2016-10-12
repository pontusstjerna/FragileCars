package view;

import model.Racetrack;
import util.CfgParser;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainWindow extends JFrame {
    public static int WORLD_WIDTH = 800;
    public static int WORLD_HEIGHT = 600;
    public static int GUI_WIDTH;

    CfgParser cfg;

    private final String title;
    private MainSurface surface;
    private UISurface ui;
    private double scale;
    private boolean fullScreen;

    public MainWindow(String title){
        this.title = title;
    }

    public void init(int width, int height){
        cfg = new CfgParser(CfgParser.STD_PATH);

        fullScreen = cfg.readBoolean("fullscreenEnabled");

        int numerator = 2;
        if(fullScreen){
            numerator = 1;
        }

        WORLD_WIDTH = width/numerator;
        WORLD_HEIGHT = height/numerator;
        GUI_WIDTH = WORLD_WIDTH/4;

        initScale(numerator);

        initWindow();
        System.out.println("View initialized with dynamic size.");
    }

    @Override
    public void repaint(){
        surface.repaint();
        ui.repaint();
    }

    private void registerKeyListener(KeyListener listener){
        surface.addKeyListener(listener);
        surface.setFocusable(true);
        surface.requestFocusInWindow();
    }

    public void startGame(Racetrack track, KeyListener listener){
        setResizable(false);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));
        surface = new MainSurface(track, scale);
        ui = new UISurface(track, scale);

        surface.setBackground(Color.black);
        ui.setBackground(Color.black);

        ui.setPreferredSize(new Dimension(WORLD_WIDTH/4,WORLD_HEIGHT));
        surface.setPreferredSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));

        ui.setAlignmentX(Component.CENTER_ALIGNMENT);
        surface.setAlignmentX(Component.CENTER_ALIGNMENT);


        registerKeyListener(listener);
        add(ui);
        add(surface);
        pack();
        if(fullScreen) setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH );
        setVisible(true);
    }

    private void initWindow() {
        setTitle(title);
        //setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initScale(int numerator){
        if(!fullScreen){
            scale = 1.0/numerator;
        }else{
            int resX = cfg.readInt("resX");
            int resY = cfg.readInt("resY");

            scale = Math.min((double)resX/(WORLD_WIDTH + GUI_WIDTH), (double)resY/WORLD_HEIGHT);

            System.out.println(scale);
        }
    }
}

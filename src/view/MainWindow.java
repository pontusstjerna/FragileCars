package view;

import model.Racetrack;

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

    private final String title;
    private MainSurface surface;
    private UISurface ui;

    public MainWindow(String title){
        this.title = title;
    }

    public void init(int width, int height, double scale, boolean fullScreen){
        int numerator = 2;
        if(fullScreen){
            numerator = 1;
        }

        WORLD_WIDTH = (int)((width/numerator)*scale);
        WORLD_HEIGHT = (int)((height/numerator)*scale);
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
        surface = new MainSurface(track);
        ui = new UISurface(track);

        ui.setBackground(Color.GREEN);

        ui.setPreferredSize(new Dimension(WORLD_WIDTH/4,WORLD_HEIGHT));
        surface.setPreferredSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));

        ui.setAlignmentX(Component.CENTER_ALIGNMENT);
        surface.setAlignmentX(Component.CENTER_ALIGNMENT);


        registerKeyListener(listener);
        add(ui);
        add(surface);
        pack();
        setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH );
        setVisible(true);
    }

    private void initWindow() {
        setTitle(title);
        //setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

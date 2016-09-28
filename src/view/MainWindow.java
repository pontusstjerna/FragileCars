package view;

import model.Racetrack;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainWindow extends JFrame implements ComponentListener {
    public static int WORLD_WIDTH = 800;
    public static int WORLD_HEIGHT = 600;

    private final String title;
    private MainSurface surface;
    private UISurface ui;

    public MainWindow(String title){
        this.title = title;
    }

    public void init(int width, int height){
        WORLD_WIDTH = width/2;
        WORLD_HEIGHT = height/2;
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
        surface.addComponentListener(this);
        surface.setFocusable(true);
        surface.requestFocusInWindow();
    }

    public void startGame(Racetrack track, KeyListener listener){
        setResizable(true);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));

        surface = new MainSurface(track);
        ui = new UISurface(track);

        ui.setPreferredSize(new Dimension(WORLD_WIDTH/4,WORLD_HEIGHT));
        surface.setPreferredSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));

        registerKeyListener(listener);
        add(ui);
        add(surface);
        pack();
        setVisible(true);
    }

    private void initWindow(){
        setTitle(title);
        //setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void componentResized(ComponentEvent e) {
       //WORLD_WIDTH = e.getComponent().getWidth() - getInsets().left - getInsets().right;
       //WORLD_HEIGHT = e.getComponent().getHeight()-getInsets().top - getInsets().bottom;
        WORLD_WIDTH = e.getComponent().getWidth();
        WORLD_HEIGHT = e.getComponent().getHeight();

    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}

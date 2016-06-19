package view;

import model.Racetrack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainWindow extends JFrame implements ComponentListener {
    public static int WINDOW_WIDTH = 800;
    public static int WINDOW_HEIGHT = 600;

    private final String title;
    private MainSurface surface;
    private UISurface ui;

    public MainWindow(String title){
        this.title = title;
    }

    public void init(){
        initWindow();
        System.out.println("View initialized with width " + WINDOW_WIDTH + " and height " + WINDOW_HEIGHT + ". ");
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

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        surface = new MainSurface(track);
        ui = new UISurface(track);

        surface.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT*7/10));

        container.add(surface);
        container.add(ui);

        add(container);
        registerKeyListener(listener);
    }

    private void initWindow(){
        setTitle(title);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        WINDOW_WIDTH = e.getComponent().getWidth();
        WINDOW_HEIGHT = e.getComponent().getHeight();
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

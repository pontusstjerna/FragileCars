package view;

import model.Racetrack;

import javax.swing.*;
import java.awt.*;

/**
 * Created by pontu on 2016-04-05.
 */
public class UISurface extends JPanel {
    Racetrack track;

    public UISurface(Racetrack track){
        this.track = track;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        displayTime(g2d);
    }

    private void displayTime(Graphics2D g){
        g.drawString("Time: " + (track.getTime()/1000) + ":" + Math.abs(track.getTime()/10 % 100),
                getWidth()/5, getHeight()/2);
    }
}

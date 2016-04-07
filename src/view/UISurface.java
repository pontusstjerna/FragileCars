package view;

import model.Racetrack;

import javax.swing.*;
import java.awt.*;

/**
 * Created by pontu on 2016-04-05.
 */
public class UISurface extends JPanel {
    private Racetrack track;

    public UISurface(Racetrack track){
        this.track = track;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        displayTime(g2d);
        displayLaps(g2d);
    }

    private void displayTime(Graphics2D g){
        g.drawString("Time: " + (track.getTime()/1000) + ":" + Math.abs(track.getTime() % 1000),
                getWidth()/5, getHeight()/2);
    }

    private void displayLaps(Graphics2D g){
        for(int i = 0; i < track.getDrawables().length; i++){
            g.drawString(track.getDrawables()[i].getName() + " - Laps: " + track.getDrawables()[i].getLaps() +
                            (track.getDrawables()[i].getFinished() != 0 ? " - FINISHED at place " +
                                    track.getDrawables()[i].getPlace() + " with time " +
                                    track.getDrawables()[i].getFinished()/1000 + ":" +
                                    (track.getDrawables()[i].getFinished() % 1000) : ""),
                    getWidth()*2/5, getHeight()/4 + i*20);
        }
    }
}

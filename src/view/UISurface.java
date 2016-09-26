package view;

import model.Racetrack;
import util.ImageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class UISurface extends JPanel {
    private Racetrack track;
    private BufferedImage guiBg;

    public UISurface(Racetrack track){
        this.track = track;
        guiBg = ImageHandler.loadImage("gui_bg");
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        //displayTime(g2d);
        //displayFPS(g2d);
        //displayLaps(g2d);
    }

    public double scale(){
        return Math.min((double)MainWindow.WINDOW_WIDTH/ guiBg.getWidth(),
                (double)MainWindow.WINDOW_HEIGHT/ guiBg.getHeight());
    }

    private void displayTime(Graphics2D g){
        g.drawString("Time: " + (track.getTime()/1000) + ":" + Math.abs(track.getTime() % 1000),
                getWidth()/5, getHeight()/2);
    }

    private void displayLaps(Graphics2D g){
        for(int i = 0; i < track.getDrawableCars().length; i++){
            g.drawString(track.getDrawableCars()[i].getName() + " - Laps: " + track.getDrawableCars()[i].getLaps() +
                            (track.getDrawableCars()[i].getFinished() != 0 ? " - FINISHED at place " +
                                    track.getDrawableCars()[i].getPlace() + " with time " +
                                    track.getDrawableCars()[i].getFinished()/1000 + ":" +
                                    (track.getDrawableCars()[i].getFinished() % 1000) : ""),
                    getWidth()/5, getHeight()/5 + i*15);
        }
    }

    private void displayFPS(Graphics2D g){
        g.drawString("FPS: " + track.getFPS(),
                getWidth()/5, getHeight()/2 + 100);
    }
}

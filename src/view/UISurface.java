package view;

import model.Racetrack;
import util.ImageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Created by pontu on 2016-04-05.
 */
public class UISurface extends JPanel {
    private Racetrack track;
    private BufferedImage guiBg, guiBgScaled;
    private int currentWidth, currentHeight;

    public UISurface(Racetrack track){
        this.track = track;
        guiBg = ImageHandler.loadImage("gui_bg");
        //guiBgScaled = scaleImage(guiBg);
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        reScaleImages();

        paintGuiBG(g2d);
        //displayTime(g2d);
        //displayFPS(g2d);
        //displayLaps(g2d);
    }

    public double scale(){
        return Math.min((double)(MainWindow.WORLD_WIDTH/4)/guiBg.getWidth(),
                (double)MainWindow.WORLD_HEIGHT/ guiBg.getHeight());
    }

    private void paintGuiBG(Graphics2D g){
        int x = 0;
        int y = 0;

        System.out.println("Scale: " + scale() + " guiw: " + MainWindow.WORLD_WIDTH/4 + " swidth: " + guiBgScaled.getWidth()*scale());

        if(MainWindow.WORLD_WIDTH/4 > guiBgScaled.getWidth()*scale()){
            x = (int)(MainWindow.WORLD_WIDTH/4 - guiBgScaled.getWidth()*scale()) + scaleX();
        }

        g.drawImage(guiBgScaled, x, y, this);
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


    private int scaleX(){
        int scaleX = (MainWindow.WORLD_WIDTH/4 - (int)(guiBg.getWidth()*scale()))/2;

        if(scaleX < 0){
            scaleX = 0;
        }

        return scaleX;
    }

    private BufferedImage scaleImage(BufferedImage unscaled){
        int w = unscaled.getWidth();
        int h = unscaled.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale(), scale());
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(unscaled, after);

        return after;
    }

    private void reScaleImages(){ //Only rescale if window size has changed!
        if(currentWidth != MainWindow.WORLD_WIDTH/4 || currentHeight != MainWindow.WORLD_HEIGHT){
            setPreferredSize(new Dimension(MainWindow.WORLD_WIDTH/4, MainWindow.WORLD_HEIGHT));

            guiBgScaled = scaleImage(guiBg);

            currentWidth = MainWindow.WORLD_WIDTH/4;
            currentHeight = MainWindow.WORLD_HEIGHT;

            System.out.println("Poowf " + currentWidth);
        }
    }

}

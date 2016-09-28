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
    private double scale;
    private BufferedImage[] numbersBig;
    private BufferedImage[] numbersSmall;

    public UISurface(Racetrack track, double scale){
        this.track = track;
        this.scale = scale;
        guiBg = ImageHandler.loadImage("gui_bg");
        guiBgScaled = scaleImage(guiBg);
        initNumbers();
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        paintGuiBG(g2d);
        displayTime(g2d);
        //displayFPS(g2d);
        //displayLaps(g2d);
    }

    private void paintGuiBG(Graphics2D g){
        g.drawImage(guiBgScaled, offsetX(), 0, this);
    }

    private void displayTime(Graphics2D g){
        g.drawImage(numbersBig[(int)(Math.abs(track.getTime()/1000000) % 10)],
                (int)((60)*scale) + offsetX(), (int)(330*scale), this);
        g.drawImage(numbersBig[(int)(Math.abs(track.getTime()/100000) % 10)],
                (int)((120)*scale) + offsetX(), (int)(330*scale), this);
        g.drawImage(numbersBig[(int)(Math.abs(track.getTime()/10000) % 10)],
                (int)((205)*scale) + offsetX(), (int)(330*scale), this);
        g.drawImage(numbersBig[(int)(Math.abs(track.getTime()/1000) % 10)],
                (int)(268*scale) + offsetX(), (int)(330*scale), this);

        //g.drawString("Time: " + (track.getTime()/1000) + ":" + Math.abs(track.getTime() % 1000),
          //      0, 0);
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

    private BufferedImage scaleImage(BufferedImage unscaled){
        int w = unscaled.getWidth();
        int h = unscaled.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(unscaled, after);

        return after;
    }

    private void initNumbers(){
        numbersBig = new BufferedImage[10];
        BufferedImage numbers = ImageHandler.loadImage("guiNumbers");
        int width = numbers.getWidth()/10;
        for(int i = 0; i < numbersBig.length; i++){
            numbersBig[i] = scaleImage(ImageHandler.cutImage(numbers, 0, i, width, numbers.getHeight()));
        }
    }

    private int offsetX(){
        return (int)((MainWindow.WORLD_WIDTH/4) - guiBgScaled.getWidth()*scale);
    }
}

package view;

import model.Racetrack;
import model.World;
import model.carcontrollers.CarController;
import model.carcontrollers.DrawableBot;
import model.carcontrollers.util.BotPoint;
import util.CfgParser;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainSurface extends JPanel {

    private Racetrack track;
    private CfgParser cfg;

    private BufferedImage[] scaledCarImgs;
    private BufferedImage scaledBackground;
    private BufferedImage scaledForeground;

    private int currentWidth;
    private int currentHeight;

    public MainSurface(Racetrack track){
        setFocusable(true);

        this.track = track;
        scaledCarImgs = new BufferedImage[track.getDrawables().length];

        cfg = new CfgParser("src\\model\\data\\config.txt");
        System.out.println("Surface initialized with scale " + scale() + ". ");
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        reScaleImages();

        paintWorld(g2d);
        paintCars(g2d);
        paintBots(g2d);
    }

    private void paintWorld(Graphics2D g){
        g.setColor(new Color(100,100,100));

        int x = MainWindow.WINDOW_WIDTH - (int)(World.WORLD_WIDTH*scale());

        if(x < 0){
            x = 0;
        }

        g.drawImage(
                scaledBackground,
                x/2,
                0, this);

        g.drawImage(
                scaledForeground,
                x/2,
                0, this);
    }

    private void paintCars(Graphics2D g){
        for(int i = 0; i < track.getDrawables().length; i++){

            //Algorithm for centering image and scaling to window.
            int x = (int)((track.getDrawables()[i].getX() - track.getDrawables()[i].getImg().getWidth()/2)*scale()) + scaleX();
            int y = (int)((track.getDrawables()[i].getY() - track.getDrawables()[i].getImg().getHeight()/2)*scale());
            int middleX = (int)(x + (track.getDrawables()[i].getImg().getWidth()/2)*scale());
            int middleY = (int)(y + (track.getDrawables()[i].getImg().getHeight()/2)*scale());

            g.rotate(track.getDrawables()[i].getHeading(), middleX, middleY);

            //Draw scaled car image
            g.drawImage(scaledCarImgs[i], x, y, this);

            g.rotate(-track.getDrawables()[i].getHeading(), middleX, middleY);
        }
    }

    private void paintBots(Graphics2D g){

        if(cfg.readBoolean("showBotPaint")){
            for(CarController bot : track.getBots()){
                bot.paint(g, scale(), scaleX());
            }
        }
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

    public double scale(){
        return Math.min((double)MainWindow.WINDOW_WIDTH/ World.WORLD_WIDTH,
                (double)MainWindow.WINDOW_HEIGHT/ World.WORLD_HEIGHT);
    }

    private int scaleX(){
        int scaleX = (MainWindow.WINDOW_WIDTH - (int)(World.WORLD_WIDTH*scale()))/2;

        if(scaleX < 0){
            scaleX = 0;
        }

        return scaleX;
    }

    private void reScaleImages(){ //Only rescale if window size has changed!
        if(currentWidth != MainWindow.WINDOW_WIDTH || currentHeight != MainWindow.WINDOW_HEIGHT){
            scaledBackground = scaleImage(track.getBackground());
            scaledForeground = scaleImage(track.getForeground());

            scaleCars();

            currentWidth = MainWindow.WINDOW_WIDTH;
            currentHeight = MainWindow.WINDOW_HEIGHT;
        }
    }

    private void scaleCars(){
        for(int i = 0; i < track.getDrawables().length; i++){
            scaledCarImgs[i] = scaleImage(track.getDrawables()[i].getImg());
        }
    }
}

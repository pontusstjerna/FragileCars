package view;

import model.GameObject;
import model.Racetrack;
import model.World;
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

    private BufferedImage[][] scaledCarImgs;
    private BufferedImage scaledBackground;
    private BufferedImage scaledForeground;

    private double scale;

    private boolean betterGraphics = true;

    public MainSurface(Racetrack track, double scale){
        setFocusable(true);

        this.scale = scale;

        this.track = track;
        scaledCarImgs = new BufferedImage[track.getDrawableCars().length][track.getDrawableCars()[0].getImgs().length];

        cfg = new CfgParser(CfgParser.STD_PATH);
        betterGraphics = cfg.readBoolean("betterGraphics");
        scaleCars();
        scaledBackground = scaleImage(track.getBackground());
        scaledForeground = scaleImage(track.getForeground());

        System.out.println("Surface initialized with scale " + scale + ". ");
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        paintWorld(g2d);
        paintObjects(g2d);
        paintCars(g2d);
    }

    private void paintWorld(Graphics2D g){
        g.setColor(new Color(100,100,100));

        g.drawImage(
                scaledBackground,
                0,
                0, this);

        g.drawImage(
                scaledForeground,
                0,
                0, this);
    }

    private void paintCars(Graphics2D g){
        for(int i = 0; i < track.getDrawableCars().length; i++){
            int frame = track.getDrawableCars()[i].getFrame();
            double heading = track.getDrawableCars()[i].getHeading();

            //Algorithm for centering image and scaling to window.
            //int x = (int)((track.getDrawableCars()[i].getX() - track.getDrawableCars()[i].getWidth()/2)*scale()) + scaleX();
            //int y = (int)((track.getDrawableCars()[i].getY() - track.getDrawableCars()[i].getHeight()/2)*scale());
            int x = (int)(track.getDrawableCars()[i].getX()*scale + scaleX());
            int y = (int)(track.getDrawableCars()[i].getY()*scale);


            double middleX = (track.getDrawableCars()[i].getX() + ((track.getDrawableCars()[i].getWidth())*
                    Math.cos(heading)*Math.cos(heading) +
                    (track.getDrawableCars()[i].getHeight())*
                            Math.sin(heading)*Math.sin(heading))/2)*scale;

            double middleY = (track.getDrawableCars()[i].getY() + ((track.getDrawableCars()[i].getWidth())*
                    Math.pow(Math.sin(heading), 2) +
                    (track.getDrawableCars()[i].getHeight())*
                            Math.pow(Math.cos(heading),2))/2)*scale;

            g.rotate(heading, middleX, middleY);

            //Draw scaled car image
            g.drawImage(scaledCarImgs[i][frame], x, y, this);
            g.rotate(-heading, middleX, middleY);
        }
    }

    //Paint other misc objects that paint themselves
    private void paintObjects(Graphics2D g){

        if(betterGraphics){
            for(GameObject bot : track.getObjects()){
                bot.paint(g, scale, scaleX());
            }
        }
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

    private int scaleX(){
        int scaleX = (MainWindow.WORLD_WIDTH - (int)(World.WORLD_WIDTH*scale))/2;

        if(scaleX < 0){
            scaleX = 0;
        }

        return 0;
    }

    private void scaleCars(){
        for(int i = 0; i < track.getDrawableCars().length; i++){
            for(int j = 0; j < track.getDrawableCars()[0].getImgs().length; j++){
                scaledCarImgs[i][j] = scaleImage(track.getDrawableCars()[i].getImgs()[j]);
            }
        }
    }
}

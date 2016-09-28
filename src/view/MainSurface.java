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

    private int currentWidth;
    private int currentHeight;

    private boolean betterGraphics = true;

    public MainSurface(Racetrack track){
        setFocusable(true);



        this.track = track;
        scaledCarImgs = new BufferedImage[track.getDrawableCars().length][track.getDrawableCars()[0].getImgs().length];

        cfg = new CfgParser("src\\model\\data\\config.txt");
        betterGraphics = cfg.readBoolean("betterGraphics");

        System.out.println("Surface initialized with scale " + scale() + ". ");
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        reScaleImages();

        paintWorld(g2d);
        paintObjects(g2d);
        paintCars(g2d);
    }

    private void paintWorld(Graphics2D g){
        g.setColor(new Color(100,100,100));

        //int x = MainWindow.WINDOW_WIDTH - (int)(World.WORLD_WIDTH*scale());

        int x = 0;
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
        for(int i = 0; i < track.getDrawableCars().length; i++){
            int frame = track.getDrawableCars()[i].getFrame();
            double heading = track.getDrawableCars()[i].getHeading();

            //Algorithm for centering image and scaling to window.
            //int x = (int)((track.getDrawableCars()[i].getX() - track.getDrawableCars()[i].getWidth()/2)*scale()) + scaleX();
            //int y = (int)((track.getDrawableCars()[i].getY() - track.getDrawableCars()[i].getHeight()/2)*scale());
            int x = (int)(track.getDrawableCars()[i].getX()*scale() + scaleX());
            int y = (int)(track.getDrawableCars()[i].getY()*scale());


            double middleX = (track.getDrawableCars()[i].getX() + ((track.getDrawableCars()[i].getWidth())*
                    Math.cos(heading)*Math.cos(heading) +
                    (track.getDrawableCars()[i].getHeight())*
                            Math.sin(heading)*Math.sin(heading))/2)*scale();

            double middleY = (track.getDrawableCars()[i].getY() + ((track.getDrawableCars()[i].getWidth())*
                    Math.pow(Math.sin(heading), 2) +
                    (track.getDrawableCars()[i].getHeight())*
                            Math.pow(Math.cos(heading),2))/2)*scale();

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
        return Math.min((double)MainWindow.WORLD_WIDTH/ World.WORLD_WIDTH,
                (double)MainWindow.WORLD_HEIGHT/ World.WORLD_HEIGHT);
    }

    private int scaleX(){
        int scaleX = (MainWindow.WORLD_WIDTH - (int)(World.WORLD_WIDTH*scale()))/2;

        if(scaleX < 0){
            scaleX = 0;
        }

        return scaleX;
    }

    private void reScaleImages(){ //Only rescale if window size has changed!
        if(currentWidth != MainWindow.WORLD_WIDTH || currentHeight != MainWindow.WORLD_HEIGHT){
            setPreferredSize(new Dimension(MainWindow.WORLD_WIDTH, MainWindow.WORLD_HEIGHT));

            scaledBackground = scaleImage(track.getBackground());
            scaledForeground = scaleImage(track.getForeground());

            scaleCars();

            currentWidth = MainWindow.WORLD_WIDTH;
            currentHeight = MainWindow.WORLD_HEIGHT;
        }
    }

    private void scaleCars(){
        for(int i = 0; i < track.getDrawableCars().length; i++){
            for(int j = 0; j < track.getDrawableCars()[0].getImgs().length; j++){
                scaledCarImgs[i][j] = scaleImage(track.getDrawableCars()[i].getImgs()[j]);
            }
        }
    }
}

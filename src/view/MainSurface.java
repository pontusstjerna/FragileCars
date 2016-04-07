package view;

import model.FragileCar;
import model.World;
import util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Created by Pontus on 2016-03-04.
 */
public class MainSurface extends JPanel {

    private FragileCar[] cars;
    private BufferedImage[] scaledCarImgs;
    private BufferedImage scaledBackground;
    private BufferedImage scaledForeground;
    private BufferedImage[] worldImages;

    private int currentWidth;
    private int currentHeight;

    public MainSurface(FragileCar[] cars, BufferedImage[] images){
        setFocusable(true);

        this.cars = cars;
        worldImages = images;
        scaledCarImgs = new BufferedImage[cars.length];

        System.out.println("Surface initialized with scale " + scale() + ". ");
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        reScaleImages();

        paintWorld(g2d);
        paintCars(g2d);
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
        for(int i = 0; i < cars.length; i++){

            //Algorithm for centering image and scaling to window.
            int x = (int)((cars[i].getX())*scale()) + scaleX();
            int y = (int)((cars[i].getY())*scale());
            int middleX = (int)(x + (cars[i].getImg().getWidth()/2)*scale());
            int middleY = (int)(y + (cars[i].getImg().getHeight()/2)*scale());

            g.rotate(cars[i].getHeading(), middleX, middleY);

            //Draw scaled car image
            g.drawImage(scaledCarImgs[i], x, y, this);

            g.rotate(-cars[i].getHeading(), middleX, middleY);
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
            scaledBackground = scaleImage(worldImages[0]);
            scaledForeground = scaleImage(worldImages[1]);

            scaleCars();

            currentWidth = MainWindow.WINDOW_WIDTH;
            currentHeight = MainWindow.WINDOW_HEIGHT;
        }
    }

    private void scaleCars(){
        for(int i = 0; i < cars.length; i++){
            scaledCarImgs[i] = scaleImage(cars[i].getImg());
        }
    }
}

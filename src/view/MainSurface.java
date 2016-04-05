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
    private BufferedImage background;
    private BufferedImage foreground;

    private boolean showVectors = true;

    public MainSurface(FragileCar[] cars, BufferedImage[] images, boolean showVectors){
        setFocusable(true);

        this.cars = cars;
        background = images[0];
        foreground = images[1];

        System.out.println("Surface initialized with scale " + scale() + ". ");

        this.showVectors = showVectors;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        paintBackground(g2d);
        paintForeground(g2d);
    }

    public void switchShowVectors(){
        showVectors = !showVectors;
    }

    private void paintVector(Vector2D vector, int startX, int startY, Graphics2D g){
        if(showVectors){
            g.drawLine(startX, startY, (int)(vector.getX()*scale()) + startX, (int)(vector.getY()*scale()) + startY);
        }
    }

    private void paintBackground(Graphics2D g){
        g.drawImage(scaleImage(background), 0, 0, this);
    }

    private void paintForeground(Graphics2D g){
        g.drawImage(scaleImage(foreground), 0, 0, this);
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

    private double scale(){
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

    private int scaleY(){
        int scaleY = (MainWindow.WINDOW_HEIGHT - (int)(World.WORLD_HEIGHT*scale()))/2;

        if(scaleY < 0){
            scaleY = 0;
        }

        return scaleY;
    }
}

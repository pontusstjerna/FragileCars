package model.misc;

import model.GameObject;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by pontu on 2016-09-24.
 */
public class SkidmarkCreator implements GameObject {

    private ArrayList<Skidmark> marks = new ArrayList<>();
    private int width;
    private Color blk = new Color(10,10,10,50);

    public SkidmarkCreator(int width){
        this.width = width;
    }

    public void drift(int x, int y, double heading){
       // marks.add(new Skidmark(x,y,width,heading));
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void paint(Graphics2D g, double scale, int scaleX) {
        g.setColor(blk);
        for(Skidmark mark : marks){
            mark.paint(g, scale, scaleX);
        }
    }

    public double getThreshold(){
        return Math.PI/4;
    }
}

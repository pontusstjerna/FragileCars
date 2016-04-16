package model.carcontrollers.util;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pontus on 2016-04-15.
 */
public class WallLine {
    private List<Point> points;
    private double thickness;

    public WallLine(Point initialPoint, double thickness){
        points = new ArrayList<>();
        points.add(initialPoint);
        this.thickness = thickness;
    }

    public void addPoint(Point p){
        points.add(p);
    }

    public List<Point> getPoints(){
        return points;
    }
}

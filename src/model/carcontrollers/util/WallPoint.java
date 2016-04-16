package model.carcontrollers.util;

import java.awt.*;

/**
 * Created by pontu on 2016-04-15.
 */
public class WallPoint extends Point {
    private double radius;

    public WallPoint(int x, int y, double radius){
        super(x,y);
        this.radius = radius;
    }

    public double getRadius(){
        return radius;
    }
}

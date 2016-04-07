package util;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by pontu on 2016-04-07.
 */
public class DirectionalRect {

    public enum Side {FRONT, BACK, LEFT, RIGHT}
    public enum Corner {FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT}
    public enum Direction {UP, DOWN, LEFT, RIGHT}

    private double x,y;
    private int w,h;
    private Point2D.Double[] corners;
    private Direction dir;

    public DirectionalRect(double x, double y, int w, int h, Direction dir){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.dir = dir;

        corners = new Point2D.Double[4];
        setCorners(dir);
    }

    public boolean isInside(double x, double y){
        return (x > this.x && x < this.x + w && y > this.y && y < this.y + h);
    }

    public Point2D.Double getCorner(Corner corner){
        switch (corner){
            case FRONT_LEFT:
                return corners[0];
            case FRONT_RIGHT:
                return corners[1];
            case BACK_LEFT:
                return corners[2];
            case BACK_RIGHT:
                return corners[3];
        }
        return null;
    }

    public Point2D.Double[] getSide(Side side){
        switch(side){
            case FRONT:
                return new Point2D.Double[] {corners[0], corners[1]};
            case BACK:
                return new Point2D.Double[] {corners[2], corners[3]};
            case LEFT:
                return new Point2D.Double[] {corners[0], corners[2]};
            case RIGHT:
                return new Point2D.Double[] {corners[1], corners[3]};
        }
        return null;
    }

    public Side intersect(double x, double y){
        //Need to have the same x or y value on at least two of the corners.

        for(Side side : Side.values()){ //Loop through all the sides
            if((getSide(side)[0].getX() == x && getSide(side)[1].getX() == x) ||
                    (getSide(side)[0].getY() == y && getSide(side)[1].getY() == y)){
                return side;
            }
        }
        return null;
    }

    private void setCorners(Direction direction)
    {
        switch(direction){
            case UP:
                corners[0] = new Point2D.Double(x,y);
                corners[1] = new Point2D.Double(x+w, y);
                corners[2] = new Point2D.Double(x, y+h);
                corners[3] = new Point2D.Double(x+w, y+h);
                break;
            case DOWN:
                corners[0] = new Point2D.Double(x+w, y+h);
                corners[1] = new Point2D.Double(x, y+h);
                corners[2] = new Point2D.Double(x+w, y);
                corners[3] = new Point2D.Double(x, y);
                break;
            case LEFT:
                corners[0] = new Point2D.Double(x,y+h);
                corners[1] = new Point2D.Double(x, y);
                corners[2] = new Point2D.Double(x+w, y+h);
                corners[3] = new Point2D.Double(x+w, y);
                break;
            case RIGHT:
                corners[0] = new Point2D.Double(x,y+h);
                corners[1] = new Point2D.Double(x, y);
                corners[2] = new Point2D.Double(x+w, y+h);
                corners[3] = new Point2D.Double(x+w, y);
                break;
        }
    }

    @Override
    public String toString(){
        return "DirectionalRect with direction " + dir + " and corners: " + "FRONT_LEFT: " +
                "(" + corners[0].getX() + "," + corners[0].getY() + ") FRONT_RIGHT: " +
                "(" + corners[1].getX() + "," + corners[1].getY() + ") BACK_LEFT: " +
                "(" + corners[2].getX() + "," + corners[2].getY() + ") BACK_RIGHT: " +
                "(" + corners[3].getX() + "," + corners[3].getY() + ").";
    }
}

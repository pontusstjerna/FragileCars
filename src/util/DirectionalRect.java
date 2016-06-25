package util;


import java.awt.Point;

/**
 * Created by pontu on 2016-04-07.
 */
public class DirectionalRect {

    public enum Side {FRONT, BACK, LEFT, RIGHT}
    public enum Corner {FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT}
    public enum Direction {UP, DOWN, LEFT, RIGHT}

    private int x,y,w,h;
    private Point[] corners;
    private Direction dir;

    public DirectionalRect(int x, int y, int w, int h, Direction dir){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.dir = dir;

        corners = new Point[4];
        setCorners(dir);
    }

    public boolean isInside(int x, int y){
        return (x > this.x && x < this.x + w && y > this.y && y < this.y + h);
    }

    public Point getCorner(Corner corner){
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

    public Point[] getSide(Side side){
        switch(side){
            case FRONT:
                return new Point[] {corners[0], corners[1]};
            case BACK:
                return new Point[] {corners[2], corners[3]};
            case LEFT:
                return new Point[] {corners[0], corners[2]};
            case RIGHT:
                return new Point[] {corners[1], corners[3]};
        }
        return null;
    }

    public Point getSide(Side side, boolean first){
        switch(side){
            case FRONT:
                if(first) {
                    return corners[0];
                }else {
                    return corners[1];
                }
            case BACK:
                if(first) {
                    return corners[2];
                }else {
                    return corners[3];
                }
            case LEFT:
                if(first) {
                    return corners[0];
                }else {
                    return corners[2];
                }
            case RIGHT:
                if(first) {
                    return corners[1];
                }else {
                    return corners[3];
                }
        }
        return null;
    }

    public Side intersect(int x, int y, int threshold){
        //Need to have the same x or y value on at least two of the corners.

        int cutX = x/threshold;
        int cutY = y/threshold;

        //BIG-ASS-ALGORITHM
        for(Side side : Side.values()){//Loop through all the sides
            int fstX = getSide(side, true).x/threshold;
            int sndX = getSide(side, false).x/threshold;
            int fstY = getSide(side, true).y/threshold;
            int sndY = getSide(side, false).y/threshold;

            if((fstX == cutX && sndX == cutX &&
                y > Math.min(getSide(side, true).getY(), getSide(side, false).getY()) &&
                y < Math.max(getSide(side, true).getY(), getSide(side, false).getY())) ||
                (fstY == cutY && sndY == cutY &&
                x > Math.min(getSide(side, true).getX(), getSide(side, false).getX()) &&
                x < Math.max(getSide(side, true).getX(), getSide(side, false).getX()))){
                return side;
            }
        }
        return null;
    }

    public Side backOrFront(int x, int y){
        if(isInside(x,y)){
            if(dir == Direction.UP || dir == Direction.DOWN )
            {
                if(Math.abs(y - getSide(Side.FRONT, true).y) < Math.abs(y - getSide(Side.BACK, true).y)){
                    return Side.FRONT;
                }else{
                    return Side.BACK;
                }
            }else{
                if(Math.abs(x - getSide(Side.FRONT, true).x) < Math.abs(x - getSide(Side.BACK, true).x)){
                    return Side.FRONT;
                }else{
                    return Side.BACK;
                }
            }
        }
        return null;
    }

    public Direction getDir(){
        return dir;
    }

    private void setCorners(Direction direction)
    {
        switch(direction){
            case UP:
                corners[0] = new Point(x,y);
                corners[1] = new Point(x+w, y);
                corners[2] = new Point(x, y+h);
                corners[3] = new Point(x+w, y+h);
                break;
            case DOWN:
                corners[0] = new Point(x+w, y+h);
                corners[1] = new Point(x, y+h);
                corners[2] = new Point(x+w, y);
                corners[3] = new Point(x, y);
                break;
            case LEFT:
                corners[0] = new Point(x,y+h);
                corners[1] = new Point(x, y);
                corners[2] = new Point(x+w, y+h);
                corners[3] = new Point(x+w, y);
                break;
            case RIGHT:
                corners[0] = new Point(x,y+h);
                corners[1] = new Point(x, y);
                corners[2] = new Point(x+w, y+h);
                corners[3] = new Point(x+w, y);
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

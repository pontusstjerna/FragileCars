package util;

/**
 * Created by pontu on 2016-09-23.
 */
public class Geom {
    public static double getPI(double angle){
        angle = angle % (Math.PI*2);
        if (angle >= Math.PI && angle > 0) {
            return angle - Math.PI*2;
        }else if(angle <= -Math.PI){
            return angle + Math.PI*2;
        }
        return angle;
    }
}

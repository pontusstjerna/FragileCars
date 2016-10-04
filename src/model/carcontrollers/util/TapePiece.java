package model.carcontrollers.util;
/**
 * Created by pontu on 2016-10-04.
 */
public class TapePiece extends BotPoint {

    private int weight = 0;

    public TapePiece(int x, int y, int radius){
        super(x,y,radius);
    }

    public void incWeight(){
        weight++;
    }

    public int getWeight(){
        return weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void decWeight(){
        weight--;
    }
}

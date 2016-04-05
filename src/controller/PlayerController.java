package controller;

import model.FragileCar;
import model.World;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pontus on 2016-03-05.
 */
public class PlayerController implements KeyListener {
    Set<Integer> movements = new HashSet<>();

    private FragileCar[] players;

    public PlayerController(FragileCar[] players){
        this.players = players;
        System.out.println("PlayerController initialized!");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        movements.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        movements.remove(e.getKeyCode());
        }

    public void update(double deltaTime){

        for(int i : movements) {
            if(players.length > 0){ //First player
                switch (i) {
                    case KeyEvent.VK_RIGHT:
                        players[0].turnRight(deltaTime);
                        break;
                    case KeyEvent.VK_LEFT:
                        players[0].turnLeft(deltaTime);
                        break;
                    case KeyEvent.VK_UP:
                        players[0].accelerate();
                        break;
                    case KeyEvent.VK_DOWN:
                        players[0].brake();
                        break;
                }
            }
            if(players.length > 1){ //Second player
                switch (i) {
                    case KeyEvent.VK_D:
                        players[1].turnRight(deltaTime);
                        break;
                    case KeyEvent.VK_A:
                        players[1].turnLeft(deltaTime);
                        break;
                    case KeyEvent.VK_W:
                        players[1].accelerate();
                        break;
                    case KeyEvent.VK_S:
                        players[1].brake();
                        break;
                }
            }
            if(players.length > 2){ //Third player
                switch (i) {
                    case KeyEvent.VK_NUMPAD6:
                        players[2].turnRight(deltaTime);
                        break;
                    case KeyEvent.VK_NUMPAD4:
                        players[2].turnLeft(deltaTime);
                        break;
                    case KeyEvent.VK_NUMPAD8:
                        players[2].accelerate();
                        break;
                    case KeyEvent.VK_NUMPAD5:
                        players[2].brake();
                        break;
                }
            }
            if(players.length > 3){ //Fourth player
                switch (i) {
                    case KeyEvent.VK_K:
                        players[0].turnRight(deltaTime);
                        break;
                    case KeyEvent.VK_H:
                        players[0].turnLeft(deltaTime);
                        break;
                    case KeyEvent.VK_U:
                        players[0].accelerate();
                        break;
                    case KeyEvent.VK_J:
                        players[0].brake();
                        break;
                }
            }
        }
    }
}

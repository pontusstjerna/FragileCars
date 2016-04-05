package controller;

import model.World;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pontus on 2016-03-05.
 */
public class PlayerController implements KeyListener {
    World world;
    Set<Integer> movements = new HashSet<>();

    public PlayerController(World world){
        this.world = world;
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

        switch(e.getKeyCode()){
            case KeyEvent.VK_SPACE:
                break;
            case KeyEvent.VK_B:
                break;
        }
    }

    public void update(double deltaTime){

        for(int i : movements) {
            switch (i) {
                case KeyEvent.VK_RIGHT:

                    break;
                case KeyEvent.VK_LEFT:

                    break;
                case KeyEvent.VK_UP:

                    break;
                case KeyEvent.VK_DOWN:

                    break;
            }
        }
    }
}

package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by pontu on 2016-03-26.
 */
public class MenuController implements ActionListener {
    private boolean done = false;
    private boolean showVectors = false;
    private int nPlayers = 1;

    boolean getShowVectors(){
        return showVectors;
    }

    int getnPlayers(){
        return nPlayers;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            actOnButton((JButton) e.getSource());
        }else if(e.getSource() instanceof JRadioButton){
            actOnRadioButton((JRadioButton) e.getSource());
        }else if(e.getSource() instanceof JToggleButton){
            actOnToggleButton((JToggleButton) e.getSource());
        }
    }

    public boolean done(){
        return done;
    }

    private void actOnButton(JButton button){
        if(button.getText() == "Start game"){
            System.out.println("Starting game!");
            done = true;
        }
    }

    private void actOnToggleButton(JToggleButton toggleButton){
        if(toggleButton.getText() == "Show vectors"){
            showVectors = !showVectors;
        }
    }

    private void actOnRadioButton(JRadioButton radioButton){
        try{
            nPlayers = Character.getNumericValue(radioButton.getText().charAt(0));
        }catch(Exception e){
            System.out.println("Unable to get value from radiobuttons!");
        }
    }
}

package se.nocroft.view;

import se.nocroft.controller.GameController;
import se.nocroft.controller.MainController;

import javax.swing.*;

public class MenuPanel extends JPanel {

    public MenuPanel(GameController gameController) {
        JButton startGameBtn = new JButton("Start");
        startGameBtn.addActionListener((e) -> {
            gameController.startGame();
        });
        add(startGameBtn);
    }
}

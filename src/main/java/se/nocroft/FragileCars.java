package se.nocroft;

import javax.swing.*;

import se.nocroft.controller.MainController;
import se.nocroft.view.MenuPanel;

/**
 * Created by pontu on 2016-04-05.
 */
public class FragileCars {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainController().init());
    }
}

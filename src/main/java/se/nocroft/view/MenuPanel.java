package se.nocroft.view;

import org.reflections.Reflections;
import se.nocroft.controller.GameController;
import se.nocroft.model.drivers.Driver;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.swing.SpringLayout.*;

public class MenuPanel extends JPanel {

    private Set<Class<? extends Driver>> botClasses;
    private String[] botClassNames;

    private final List<JPanel> classNameRows = new ArrayList<>();
    private final List<JComboBox<String>> pickers = new ArrayList<>();
    private final SpringLayout layout = new SpringLayout();
    private final JButton addBotBtn = new JButton("Add another driver");
    private final JLabel welcomeLabel = new JLabel("Welcome to Fragile Carz.");
    private final JLabel subtitleLabel = new JLabel("Please select your drivers below.");

    public MenuPanel(GameController gameController) {
        initClassNames();
        setLayout(layout);

        JButton startGameBtn = new JButton("Start");

        addBotBtn.addActionListener(e -> {
            addClassPickerRow();
        });

        startGameBtn.addActionListener(e ->
            gameController.startGame()
        );

        add(welcomeLabel);
        layout.putConstraint(NORTH, welcomeLabel, 10, NORTH, this);
        layout.putConstraint(HORIZONTAL_CENTER, welcomeLabel, 0, HORIZONTAL_CENTER, this);
        add(subtitleLabel);
        layout.putConstraint(NORTH, subtitleLabel, 10, SOUTH, welcomeLabel);
        layout.putConstraint(HORIZONTAL_CENTER, subtitleLabel, 0, HORIZONTAL_CENTER, this);
        addClassPickerRow();
        add(addBotBtn);
        add(startGameBtn);
        layout.putConstraint(NORTH, addBotBtn, 10, SOUTH, classNameRows.get(classNameRows.size() - 1));
        layout.putConstraint(HORIZONTAL_CENTER, addBotBtn, 0, HORIZONTAL_CENTER, this);
        layout.putConstraint(SOUTH, startGameBtn, 0, SOUTH, this);
        layout.putConstraint(HORIZONTAL_CENTER, startGameBtn, 0, HORIZONTAL_CENTER, this);
    }

    private void initClassNames() {
        Reflections reflections = new Reflections("se.nocroft.model.drivers");
        botClasses = reflections.getSubTypesOf(Driver.class);
        botClassNames = new String[botClasses.size() + 1];
        botClassNames = botClasses.stream().map(Class::getName).toArray(String[]::new);
        botClassNames[botClassNames.length - 1] = "Player controlled car";
    }

    private void updateAddBotBtnConstraints() {
        layout.removeLayoutComponent(addBotBtn);
        layout.putConstraint(NORTH, addBotBtn, 10, SOUTH, classNameRows.get(classNameRows.size() - 1));
        layout.putConstraint(HORIZONTAL_CENTER, addBotBtn, 0, HORIZONTAL_CENTER, this);
    }

    private void addClassPickerRow() {
        JPanel container = new JPanel();
        JComboBox<String> comboBox = new JComboBox<>(botClassNames);
        JSpinner spinner = new JSpinner();
        spinner.setValue(1);
        JButton removeBotBtn = new JButton("Remove");

        removeBotBtn.addActionListener(e -> {
            classNameRows.remove(container);
            updateAddBotBtnConstraints();
            remove(container);
            revalidate();
            repaint();
        });

        pickers.add(comboBox);

        container.add(comboBox);
        container.add(spinner);

        add(container);

        if (!classNameRows.isEmpty()) {
            container.add(removeBotBtn);
            layout.putConstraint(NORTH, container, 5, SOUTH, classNameRows.get(classNameRows.size() - 1));
        } else {
            layout.putConstraint(NORTH, container, 20, SOUTH, subtitleLabel);
        }
        layout.putConstraint(HORIZONTAL_CENTER, container, 0, HORIZONTAL_CENTER, this);

        classNameRows.add(container);

        updateAddBotBtnConstraints();
        revalidate();
    }
}

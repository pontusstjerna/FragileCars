package se.nocroft.view;

import org.reflections.Reflections;
import se.nocroft.controller.GameController;
import se.nocroft.model.cars.Car;
import se.nocroft.model.cars.CarSetup;
import se.nocroft.model.drivers.Driver;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javax.swing.SpringLayout.*;

public class MenuPanel extends JPanel {

    private List<Class<? extends Driver>> botClasses;
    private String[] botClassNames;
    private String[] typeNames;

    private final List<JPanel> classNameRows = new ArrayList<>();
    private final List<JComboBox<String>> classPickers = new ArrayList<>();
    private final List<JComboBox<String>> colorPickers = new ArrayList<>();
    private final List<JSpinner> spinners = new ArrayList<>();
    private final SpringLayout layout = new SpringLayout();
    private final JButton addBotBtn = new JButton("Add another driver");
    private final JLabel welcomeLabel = new JLabel("Welcome to Fragile Carz.");
    private final JLabel subtitleLabel = new JLabel("Please select your drivers below.");

    public MenuPanel(GameController gameController) {
        initClassNames();
        initTypeNames();
        setLayout(layout);

        JButton startGameBtn = new JButton("Start");

        addBotBtn.addActionListener(e -> {
            addClassPickerRow();
        });

        startGameBtn.addActionListener(e -> {
                    CarSetup[] carSetups = IntStream.range(0, classPickers.size()).boxed().flatMap(i -> {
                        int classIndex = classPickers.get(i).getSelectedIndex();
                        int number = (Integer) spinners.get(i).getValue();
                        Car.Cars type = Car.Cars.values()[colorPickers.get(i).getSelectedIndex()];

                        return IntStream.range(0, number).boxed().map(j -> new CarSetup(type, botClasses.get(classIndex)));
                    }).toArray(CarSetup[]::new);

                    gameController.startGame(carSetups);
                }
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
        botClasses = new ArrayList<>(reflections.getSubTypesOf(Driver.class));
        botClassNames = new String[botClasses.size() + 1];
        botClassNames = botClasses.stream().map(Class::getName).toArray(String[]::new);
        botClassNames[botClassNames.length - 1] = "Player controlled car";
    }

    private void initTypeNames() {
        typeNames = Arrays.stream(Car.Cars.values()).map(Car.Cars::toString).toArray(String[]::new);
    }

    private void updateAddBotBtnConstraints() {
        layout.removeLayoutComponent(addBotBtn);
        layout.putConstraint(NORTH, addBotBtn, 10, SOUTH, classNameRows.get(classNameRows.size() - 1));
        layout.putConstraint(HORIZONTAL_CENTER, addBotBtn, 0, HORIZONTAL_CENTER, this);
    }

    private void addClassPickerRow() {
        JPanel container = new JPanel();
        JComboBox<String> classPicker = new JComboBox<>(botClassNames);
        JComboBox<String> colorPicker = new JComboBox<>(typeNames);
        JSpinner spinner = new JSpinner();
        spinner.setValue(1);
        JButton removeBotBtn = new JButton("Remove");

        removeBotBtn.addActionListener(e -> {
            classNameRows.remove(container);
            classPickers.remove(classPicker);
            colorPickers.remove(colorPicker);
            spinners.remove(spinner);
            updateAddBotBtnConstraints();
            remove(container);
            revalidate();
            repaint();
        });

        classPickers.add(classPicker);
        colorPickers.add(colorPicker);
        spinners.add(spinner);

        container.add(classPicker);
        container.add(colorPicker);
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

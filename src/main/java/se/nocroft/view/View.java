package se.nocroft.view;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import model.Racetrack;

import java.awt.event.KeyListener;

/**
 * Created by pontu on 2016-09-15.
 */
public class View extends Application {
    private int WINDOW_WIDTH = 1000;
    private int WINDOW_HEIGHT = 600;

    private String title;

    public View(String title, Racetrack track, KeyListener listener) {
        this.title = title;

        new Thread(() -> launch()).start();
    }

    public View() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(title);
        Group root = new Group();
        Canvas window = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = window.getGraphicsContext2D();
        root.getChildren().add(window);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawShapes(GraphicsContext gc) {
        gc.fillRect(50, 50, 50, 50);
    }
}

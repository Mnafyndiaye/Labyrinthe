package org.example.labyrinthsolver;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LabyrinthView labyrinthView = new LabyrinthView();
        Scene scene = new Scene(labyrinthView, 500, 500); // Taille ajustée pour le nouveau design
        primaryStage.setTitle("Labyrinth Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
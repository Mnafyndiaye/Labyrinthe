package org.example.labyrinthe2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Classe principale pour lancer l'application JavaFX
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LabyrinthView labyrinthView = new LabyrinthView(); // Crée l'interface graphique
        Scene scene = new Scene(labyrinthView, 600, 500); // Crée une scène de 600x500 pixels
        primaryStage.setTitle("Labyrinth Solver"); // Définit le titre de la fenêtre
        primaryStage.setScene(scene); // Associe la scène à la fenêtre
        primaryStage.show(); // Affiche la fenêtre
    }

    // Point d'entrée de l'application
    public static void main(String[] args) {
        launch(args); // Lance l'application JavaFX
    }
}
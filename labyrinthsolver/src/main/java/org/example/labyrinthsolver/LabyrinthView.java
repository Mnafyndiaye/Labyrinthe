package org.example.labyrinthsolver;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.List;

public class LabyrinthView extends VBox {
    private Canvas canvas;
    private Labyrinth labyrinth;
    private final int cellSize = 50;
    private char[][] maze;
    private Label messageLabel;

    public LabyrinthView() {
        maze = new char[][]{
                {'#', '#', '#', '#', '#', '#'},
                {'S', '=', '#', '=', '=', '#'},
                {'#', '=', '#', '=', '#', '#'},
                {'#', '=', '=', '=', 'E', '#'},
                {'#', '#', '#', '#', '#', '#'}
        };
        labyrinth = new Labyrinth(maze);
        initialize();
    }

    private void initialize() {
        canvas = new Canvas(labyrinth.getCols() * cellSize, labyrinth.getRows() * cellSize);
        this.setStyle("-fx-background-color: lightgray;");

        // Label pour les messages
        messageLabel = new Label("Chargez un labyrinthe ou générez-en un.");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        // Boutons
        Button loadButton = new Button("Load Maze from File");
        Button generateButton = new Button("Generate Random Maze");
        Button dfsButton = new Button("Solve with DFS");
        Button bfsButton = new Button("Solve with BFS");
        Button resetButton = new Button("Reset");

        // Action pour charger depuis un fichier
        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Maze File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                labyrinth.loadFromFile(file.getAbsolutePath());
                updateCanvas();
                drawLabyrinth();
                messageLabel.setText("Labyrinthe chargé. Cliquez sur un bouton pour résoudre.");
            }
        });

        // Action pour générer un labyrinthe aléatoire
        generateButton.setOnAction(event -> {
            labyrinth.generateRandomMaze(7, 7);
            updateCanvas();
            drawLabyrinth();
            messageLabel.setText("Labyrinthe généré. Cliquez sur un bouton pour résoudre.");
        });

        // Action pour résoudre avec DFS
        dfsButton.setOnAction(event -> {
            Labyrinth.SolveResult result = labyrinth.solveDFS(); // Utiliser SolveResult
            drawLabyrinth();
            if (result.isPathFound()) {
                drawPath(result.getPath());
                messageLabel.setText("DFS: " + result.getSteps() + " steps, " + String.format("%.3f", result.getTimeMs()) + " ms");
            } else {
                messageLabel.setText("DFS: Aucun chemin trouvé !");
            }
        });

        // Action pour résoudre avec BFS
        bfsButton.setOnAction(event -> {
            Labyrinth.SolveResult result = labyrinth.solveBFS(); // Utiliser SolveResult
            drawLabyrinth();
            if (result.isPathFound()) {
                drawPath(result.getPath());
                messageLabel.setText("BFS: " + result.getSteps() + " steps, " + String.format("%.3f", result.getTimeMs()) + " ms");
            } else {
                messageLabel.setText("BFS: Aucun chemin trouvé !");
            }
        });

        // Action pour réinitialiser
        resetButton.setOnAction(event -> {
            drawLabyrinth();
            messageLabel.setText("Labyrinthe réinitialisé. Cliquez sur un bouton pour résoudre.");
        });

        // Ajouter les éléments au VBox
        getChildren().clear();
        getChildren().addAll(canvas, messageLabel, loadButton, generateButton, dfsButton, bfsButton, resetButton);

        // Dessiner le labyrinthe initial
        drawLabyrinth();
    }

    private void updateCanvas() {
        canvas = new Canvas(labyrinth.getCols() * cellSize, labyrinth.getRows() * cellSize);
        getChildren().set(0, canvas);
    }

    private void drawLabyrinth() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.LIGHTGRAY);
        char[][] currentMaze = labyrinth.getMaze();
        for (int i = 0; i < currentMaze.length; i++) {
            for (int j = 0; j < currentMaze[0].length; j++) {
                if (currentMaze[i][j] == '#') {
                    gc.setFill(Color.DARKGRAY);
                } else if (currentMaze[i][j] == 'S') {
                    gc.setFill(Color.GREEN);
                } else if (currentMaze[i][j] == 'E') {
                    gc.setFill(Color.RED);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                gc.strokeRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawPath(List<int[]> path) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        char[][] currentMaze = labyrinth.getMaze();
        for (int[] pos : path) {
            int x = pos[0], y = pos[1];
            if (currentMaze[x][y] != 'S' && currentMaze[x][y] != 'E') {
                gc.fillRect(y * cellSize + 10, x * cellSize + 10, cellSize - 20, cellSize - 20);
            }
        }
    }
}
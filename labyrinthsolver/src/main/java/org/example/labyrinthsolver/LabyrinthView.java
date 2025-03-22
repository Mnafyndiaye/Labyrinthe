package org.example.labyrinthsolver;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class LabyrinthView extends BorderPane {
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
        this.setStyle("-fx-background-color: #f0f4f8;");

        canvas = new Canvas(labyrinth.getCols() * cellSize, labyrinth.getRows() * cellSize);
        VBox canvasContainer = new VBox(canvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setPadding(new Insets(20));
        this.setCenter(canvasContainer);

        messageLabel = new Label("Chargez un labyrinthe ou générez-en un.");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        messageLabel.setTextFill(Color.DARKSLATEBLUE);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setStyle("-fx-background-color: #e6ecf0; -fx-background-radius: 5;");
        HBox messageBox = new HBox(messageLabel);
        messageBox.setAlignment(Pos.CENTER);
        this.setTop(messageBox);

        // Boutons avec icônes
        Button loadButton = createButtonWithIcon("Load Maze", "/icons/file.png");
        Button generateButton = createButtonWithIcon("Generate Maze", "/icons/labyrinth.png");
        Button dfsButton = createButtonWithIcon("Solve with DFS", "/icons/DFS.png");
        Button bfsButton = createButtonWithIcon("Solve with BFS", "/icons/BFS.png");
        Button resetButton = createButtonWithIcon("Reset", "/icons/circular.png");

        // Style des boutons
        styleButton(loadButton, "#4CAF50");
        styleButton(generateButton, "#2196F3");
        styleButton(dfsButton, "#FF9800");
        styleButton(bfsButton, "#F44336");
        styleButton(resetButton, "#9E9E9E");

        HBox buttonBox = new HBox(10, loadButton, generateButton, dfsButton, bfsButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: #d3dce6; -fx-background-radius: 5;");
        this.setBottom(buttonBox);

        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Maze File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                labyrinth.loadFromFile(file.getAbsolutePath());
                updateCanvas();
                drawLabyrinth();
                messageLabel.setText("Labyrinthe chargé !");
                messageLabel.setTextFill(Color.DARKGREEN);
            }
        });

        generateButton.setOnAction(event -> {
            labyrinth.generateRandomMaze(7, 7);
            updateCanvas();
            drawLabyrinth();
            messageLabel.setText("Labyrinthe généré !");
            messageLabel.setTextFill(Color.DARKGREEN);
        });

        dfsButton.setOnAction(event -> {
            Labyrinth.SolveResult result = labyrinth.solveDFS();
            drawLabyrinth();
            if (result.isPathFound()) {
                drawPath(result.getPath());
                messageLabel.setText("DFS: " + result.getSteps() + " steps, " + String.format("%.3f", result.getTimeMs()) + " ms");
                messageLabel.setTextFill(Color.DARKGREEN);
            } else {
                messageLabel.setText("DFS: Aucun chemin trouvé !");
                messageLabel.setTextFill(Color.RED);
            }
        });

        bfsButton.setOnAction(event -> {
            Labyrinth.SolveResult result = labyrinth.solveBFS();
            drawLabyrinth();
            if (result.isPathFound()) {
                drawPath(result.getPath());
                messageLabel.setText("BFS: " + result.getSteps() + " steps, " + String.format("%.3f", result.getTimeMs()) + " ms");
                messageLabel.setTextFill(Color.DARKGREEN);
            } else {
                messageLabel.setText("BFS: Aucun chemin trouvé !");
                messageLabel.setTextFill(Color.RED);
            }
        });

        resetButton.setOnAction(event -> {
            drawLabyrinth();
            messageLabel.setText("Labyrinthe réinitialisé.");
            messageLabel.setTextFill(Color.DARKSLATEBLUE);
        });

        drawLabyrinth();
    }

    private Button createButtonWithIcon(String text, String iconPath) {
        Button button = new Button(text);
        try {
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(16);
            iconView.setFitHeight(16);
            button.setGraphic(iconView);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'icône : " + iconPath);
            e.printStackTrace();
        }
        return button;
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + darkenColor(color) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
    }

    private String darkenColor(String color) {
        return color.replace("#", "#D").replace("F", "C");
    }

    private void updateCanvas() {
        canvas = new Canvas(labyrinth.getCols() * cellSize, labyrinth.getRows() * cellSize);
        ((VBox) this.getCenter()).getChildren().set(0, canvas);
    }

    private void drawLabyrinth() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        char[][] currentMaze = labyrinth.getMaze();
        for (int i = 0; i < currentMaze.length; i++) {
            for (int j = 0; j < currentMaze[0].length; j++) {
                if (currentMaze[i][j] == '#') {
                    gc.setFill(Color.DARKGRAY);
                } else if (currentMaze[i][j] == 'S') {
                    gc.setFill(Color.DARKGREEN);
                } else if (currentMaze[i][j] == 'E') {
                    gc.setFill(Color.DARKRED);
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
        gc.setFill(Color.DODGERBLUE);
        char[][] currentMaze = labyrinth.getMaze();

        Timeline timeline = new Timeline();
        final int[] index = {0};
        timeline.setCycleCount(path.size());

        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), event -> {
            if (index[0] < path.size()) {
                int[] pos = path.get(index[0]);
                int x = pos[0], y = pos[1];
                if (currentMaze[x][y] != 'S' && currentMaze[x][y] != 'E') {
                    gc.fillRect(y * cellSize + 10, x * cellSize + 10, cellSize - 20, cellSize - 20);
                }
                index[0]++;
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
}
package org.example.labyrinthe2;

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

// Classe pour gérer l'interface graphique du labyrinthe
public class LabyrinthView extends BorderPane {
    private Canvas canvas; // Canvas pour dessiner le labyrinthe
    private Labyrinth labyrinth; // Instance de la classe Labyrinth pour la logique
    private final int cellSize = 25; // Taille d'une cellule du labyrinthe (réduite pour 19x19)
    private char[][] maze; // Tableau 2D représentant le labyrinthe
    private Label messageLabel; // Label pour afficher les messages (succès, erreurs, etc.)
    private boolean isDarkTheme = false; // État du thème (clair par défaut)

    // Classe interne pour stocker les styles des thèmes (clair et sombre)
    private static class ThemeStyles {
        String appBackground; // Couleur de fond de l'application
        String messageBackground; // Couleur de fond du message
        String messageText; // Couleur du texte du message
        String buttonBarBackground; // Couleur de fond de la barre de boutons
        String wallColor; // Couleur des murs
        String pathColor; // Couleur du chemin
        String startColor; // Couleur du départ
        String endColor; // Couleur de la sortie
        String emptyColor; // Couleur des cases vides

        ThemeStyles(String appBackground, String messageBackground, String messageText,
                    String buttonBarBackground, String wallColor, String pathColor,
                    String startColor, String endColor, String emptyColor) {
            this.appBackground = appBackground;
            this.messageBackground = messageBackground;
            this.messageText = messageText;
            this.buttonBarBackground = buttonBarBackground;
            this.wallColor = wallColor;
            this.pathColor = pathColor;
            this.startColor = startColor;
            this.endColor = endColor;
            this.emptyColor = emptyColor;
        }
    }

    // Définition du thème clair (couleurs par défaut)
    private final ThemeStyles lightTheme = new ThemeStyles(
            "#f0f4f8", "#e6ecf0", "DARKSLATEBLUE", "#d3dce6",
            "DARKGRAY", "DODGERBLUE", "DARKGREEN", "DARKRED", "WHITE"
    );

    // Définition du thème sombre
    private final ThemeStyles darkTheme = new ThemeStyles(
            "#2b2b2b", "#3c3f41", "LIGHTBLUE", "#4b4e50",
            "GRAY", "CYAN", "LIME", "RED", "BLACK"
    );

    private ThemeStyles currentTheme; // Thème actuellement utilisé

    // Constructeur : initialise le labyrinthe par défaut et l'interface
    public LabyrinthView() {
        maze = new char[][]{
                {'#', '#', '#', '#', '#', '#'},
                {'S', '=', '#', '=', '=', '#'},
                {'#', '=', '#', '=', '#', '#'},
                {'#', '=', '=', '=', 'E', '#'},
                {'#', '#', '#', '#', '#', '#'}
        };
        labyrinth = new Labyrinth(maze);
        currentTheme = lightTheme; // Thème clair par défaut
        initialize();
    }

    // Initialise l'interface graphique (canvas, message, boutons)
    private void initialize() {
        applyTheme(); // Applique le thème initial

        // Crée le canvas pour dessiner le labyrinthe
        canvas = new Canvas(labyrinth.getCols() * cellSize, labyrinth.getRows() * cellSize);
        VBox canvasContainer = new VBox(canvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setPadding(new Insets(20));
        this.setCenter(canvasContainer);

        // Crée le label pour les messages
        messageLabel = new Label("Chargez un labyrinthe ou générez-en un.");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        messageLabel.setTextFill(Color.valueOf(currentTheme.messageText));
        messageLabel.setPadding(new Insets(10));
        messageLabel.setStyle("-fx-background-color: " + currentTheme.messageBackground + "; -fx-background-radius: 5;");
        HBox messageBox = new HBox(messageLabel);
        messageBox.setAlignment(Pos.CENTER);
        this.setTop(messageBox);

        // Crée les boutons avec leurs icônes
        Button loadButton = createButtonWithIcon("Load Maze", "/icons/file.png");
        Button generateButton = createButtonWithIcon("Generate Maze", "/icons/labyrinth.png");
        Button dfsButton = createButtonWithIcon("Solve with DFS", "/icons/DFS.png");
        Button bfsButton = createButtonWithIcon("Solve with BFS", "/icons/BFS.png");
        Button resetButton = createButtonWithIcon("Reset", "/icons/circular.png");
        Button themeButton = createButtonWithIcon("Toggle Theme", "/icons/theme.png");

        // Applique un style aux boutons
        styleButton(loadButton, "#4CAF50");
        styleButton(generateButton, "#2196F3");
        styleButton(dfsButton, "#FF9800");
        styleButton(bfsButton, "#F44336");
        styleButton(resetButton, "#9E9E9E");
        styleButton(themeButton, "#FF5722");

        // Ajoute les boutons dans une barre horizontale
        HBox buttonBox = new HBox(10, loadButton, generateButton, dfsButton, bfsButton, resetButton, themeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: " + currentTheme.buttonBarBackground + "; -fx-background-radius: 5;");
        this.setBottom(buttonBox);

        // Action du bouton "Load Maze" : charge un labyrinthe depuis un fichier
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

        // Action du bouton "Generate Maze" : génère un labyrinthe aléatoire
        generateButton.setOnAction(event -> {
            labyrinth.generateRandomMaze(19, 19); // Changement de 7x7 à 19x19
            updateCanvas();
            drawLabyrinth();
            messageLabel.setText("Labyrinthe généré !");
            messageLabel.setTextFill(Color.DARKGREEN);
        });

        // Action du bouton "Solve with DFS" : résout le labyrinthe avec DFS
        dfsButton.setOnAction(event -> {
            Labyrinth.SolveResult result = labyrinth.solveDFS();
            drawLabyrinth();
            if (result.isPathFound()) {
                drawPath(result.getPath());
                // Calcule la longueur du chemin
                int pathLength = result.getPath().size();
                messageLabel.setText("DFS: " + result.getSteps() + " steps, " + String.format("%.3f", result.getTimeMs()) + " ms, Chemin: " + pathLength + " cases");
                messageLabel.setTextFill(Color.DARKGREEN);
            } else {
                messageLabel.setText("DFS: Aucun chemin trouvé !");
                messageLabel.setTextFill(Color.RED);
            }
        });

        // Action du bouton "Solve with BFS" : résout le labyrinthe avec BFS
        bfsButton.setOnAction(event -> {
            Labyrinth.SolveResult result = labyrinth.solveBFS();
            drawLabyrinth();
            if (result.isPathFound()) {
                drawPath(result.getPath());
                // Calcule la longueur du chemin
                int pathLength = result.getPath().size();
                messageLabel.setText("BFS: " + result.getSteps() + " steps, " + String.format("%.3f", result.getTimeMs()) + " ms, Chemin: " + pathLength + " cases");
                messageLabel.setTextFill(Color.DARKGREEN);
            } else {
                messageLabel.setText("BFS: Aucun chemin trouvé !");
                messageLabel.setTextFill(Color.RED);
            }
        });

        // Action du bouton "Reset" : réinitialise l'affichage du labyrinthe
        resetButton.setOnAction(event -> {
            drawLabyrinth();
            messageLabel.setText("Labyrinthe réinitialisé.");
            messageLabel.setTextFill(Color.valueOf(currentTheme.messageText));
        });

        // Action du bouton "Toggle Theme" : bascule entre thème clair et sombre
        themeButton.setOnAction(event -> {
            isDarkTheme = !isDarkTheme;
            currentTheme = isDarkTheme ? darkTheme : lightTheme;
            applyTheme();
            drawLabyrinth();
            messageLabel.setText(isDarkTheme ? "Thème sombre activé !" : "Thème clair activé !");
            messageLabel.setTextFill(Color.valueOf(currentTheme.messageText));
        });

        drawLabyrinth(); // Dessine le labyrinthe initial
    }

    // Applique le thème courant à l'interface graphique
    private void applyTheme() {
        this.setStyle("-fx-background-color: " + currentTheme.appBackground + ";");
        if (this.getTop() != null) {
            ((HBox) this.getTop()).getChildren().get(0).setStyle(
                    "-fx-background-color: " + currentTheme.messageBackground + "; -fx-background-radius: 5;"
            );
            ((Label) ((HBox) this.getTop()).getChildren().get(0)).setTextFill(Color.valueOf(currentTheme.messageText));
        }
        if (this.getBottom() != null) {
            ((HBox) this.getBottom()).setStyle(
                    "-fx-background-color: " + currentTheme.buttonBarBackground + "; -fx-background-radius: 5;"
            );
        }
    }

    // Crée un bouton avec une icône
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

    // Applique un style au bouton (couleur, effet de survol)
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

    // Assombrit une couleur en réduisant ses composantes RGB
    private String darkenColor(String color) {
        try {
            // Convertit la couleur hexadécimale en objet Color
            Color originalColor = Color.web(color);
            // Réduit la luminosité de 20% (par exemple)
            double factor = 0.8; // Facteur d'assombrissement (0.8 = 80% de la luminosité)
            double red = originalColor.getRed() * factor;
            double green = originalColor.getGreen() * factor;
            double blue = originalColor.getBlue() * factor;
            // Crée une nouvelle couleur assombrie
            Color darkerColor = new Color(
                    Math.max(0, Math.min(1, red)),
                    Math.max(0, Math.min(1, green)),
                    Math.max(0, Math.min(1, blue)),
                    1.0
            );
            // Convertit la couleur en format hexadécimal (#RRGGBB)
            return String.format("#%02X%02X%02X",
                    (int) (darkerColor.getRed() * 255),
                    (int) (darkerColor.getGreen() * 255),
                    (int) (darkerColor.getBlue() * 255));
        } catch (Exception e) {
            System.out.println("Erreur lors de l'assombrissement de la couleur : " + color);
            return color; // Retourne la couleur d'origine en cas d'erreur
        }
    }

    // Met à jour la taille du canvas en fonction des dimensions du labyrinthe
    private void updateCanvas() {
        canvas = new Canvas(labyrinth.getCols() * cellSize, labyrinth.getRows() * cellSize);
        ((VBox) this.getCenter()).getChildren().set(0, canvas);
    }

    // Dessine le labyrinthe sur le canvas avec les couleurs du thème courant
    private void drawLabyrinth() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        char[][] currentMaze = labyrinth.getMaze();
        for (int i = 0; i < currentMaze.length; i++) {
            for (int j = 0; j < currentMaze[0].length; j++) {
                if (currentMaze[i][j] == '#') {
                    gc.setFill(Color.valueOf(currentTheme.wallColor));
                } else if (currentMaze[i][j] == 'S') {
                    gc.setFill(Color.valueOf(currentTheme.startColor));
                } else if (currentMaze[i][j] == 'E') {
                    gc.setFill(Color.valueOf(currentTheme.endColor));
                } else {
                    gc.setFill(Color.valueOf(currentTheme.emptyColor));
                }
                gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                gc.strokeRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }

    // Dessine le chemin trouvé avec une animation progressive
    private void drawPath(List<int[]> path) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.valueOf(currentTheme.pathColor));
        char[][] currentMaze = labyrinth.getMaze();

        Timeline timeline = new Timeline();
        final int[] index = {0};
        timeline.setCycleCount(path.size());

        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> { // Animation plus rapide pour 19x19
            if (index[0] < path.size()) {
                int[] pos = path.get(index[0]);
                int x = pos[0], y = pos[1];
                if (currentMaze[x][y] != 'S' && currentMaze[x][y] != 'E') {
                    gc.fillRect(y * cellSize + 5, x * cellSize + 5, cellSize - 10, cellSize - 10);
                }
                index[0]++;
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
}
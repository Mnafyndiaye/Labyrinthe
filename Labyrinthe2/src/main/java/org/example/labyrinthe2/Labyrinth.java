package org.example.labyrinthe2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

// Classe principale pour gérer la logique du labyrinthe (génération, chargement, résolution)
public class Labyrinth {
    private char[][] maze; // Tableau 2D représentant le labyrinthe
    private int rows, cols; // Dimensions du labyrinthe (lignes et colonnes)
    private int startX, startY, endX, endY; // Coordonnées du départ (S) et de la sortie (E)
    private Random random = new Random(); // Générateur de nombres aléatoires

    // Constructeur qui initialise le labyrinthe avec un tableau donné
    public Labyrinth(char[][] maze) {
        if (maze != null) {
            this.maze = maze;
            this.rows = maze.length;
            this.cols = maze[0].length;
            findStartAndEnd(); // Recherche des positions de départ et de sortie
        }
    }

    // Recherche les positions de départ ('S') et de sortie ('E') dans le labyrinthe
    private void findStartAndEnd() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 'S') {
                    startX = i;
                    startY = j;
                } else if (maze[i][j] == 'E') {
                    endX = i;
                    endY = j;
                }
            }
        }
    }

    // Charge un labyrinthe depuis un fichier texte
    public void loadFromFile(String filename) {
        List<char[]> lines = new ArrayList<>(); // Liste pour stocker les lignes du fichier
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.toCharArray()); // Ajoute chaque ligne sous forme de tableau de caractères
            }
        } catch (Exception e) {
            e.printStackTrace(); // Affiche les erreurs en cas de problème de lecture
        }
        this.maze = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            this.maze[i] = lines.get(i); // Convertit la liste en tableau 2D
        }
        this.rows = maze.length;
        this.cols = maze[0].length;
        findStartAndEnd(); // Met à jour les positions de départ et de sortie
    }

    // Génère un labyrinthe aléatoire de taille donnée
    public void generateRandomMaze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new char[rows][cols];

        // Remplit le labyrinthe de murs ('#')
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
            }
        }

        // Étape 1 : Génère un labyrinthe de base avec Recursive Backtracking
        recursiveBacktracking(1, 1);

        // Étape 2 : Ajoute des motifs complexes, chemins alternatifs, impasses et îlots
        addComplexity();

        // Place le départ (S) et la sortie (E) aux positions fixes
        maze[1][1] = 'S';
        maze[rows - 2][cols - 2] = 'E'; // Pour 19x19, E sera à (17,17)

        findStartAndEnd(); // Met à jour les positions de départ et de sortie
    }

    // Algorithme Recursive Backtracking pour générer un labyrinthe de base
    private void recursiveBacktracking(int x, int y) {
        maze[x][y] = '='; // Ouvre une case (chemin)

        // Liste des directions possibles (haut, droite, bas, gauche)
        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        // Mélange les directions pour un résultat aléatoire
        List<int[]> shuffledDirections = new ArrayList<>(Arrays.asList(directions));
        Collections.shuffle(shuffledDirections, random);

        for (int[] dir : shuffledDirections) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Vérifie si la nouvelle position est dans les limites et est un mur
            if (newX > 0 && newX < rows - 1 && newY > 0 && newY < cols - 1 && maze[newX][newY] == '#') {
                // Ouvre le mur entre la position actuelle et la nouvelle position
                maze[x + dir[0] / 2][y + dir[1] / 2] = '=';
                recursiveBacktracking(newX, newY); // Récursion sur la nouvelle position
            }
        }
    }

    // Ajoute des motifs complexes, chemins alternatifs, impasses et îlots
    private void addComplexity() {
        // Étape 1 : Ajoute des motifs complexes (croix, T, spirales)
        addComplexPatterns();

        // Étape 2 : Ouvre des murs aléatoires pour créer des chemins alternatifs
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (maze[i][j] == '#' && random.nextDouble() < 0.5) { // 50% de chance d'ouvrir un mur
                    // Ouvre si au moins un voisin est un chemin
                    int openNeighbors = 0;
                    if (i > 0 && maze[i - 1][j] == '=') openNeighbors++;
                    if (i < rows - 1 && maze[i + 1][j] == '=') openNeighbors++;
                    if (j > 0 && maze[i][j - 1] == '=') openNeighbors++;
                    if (j < cols - 1 && maze[i][j + 1] == '=') openNeighbors++;
                    if (openNeighbors >= 1) {
                        maze[i][j] = '=';
                    }
                }
            }
        }

        // Étape 3 : Ajoute des impasses longues
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (maze[i][j] == '=' && random.nextDouble() < 0.2) { // 20% de chance de creuser une impasse
                    createLongDeadEnd(i, j);
                }
            }
        }

        // Étape 4 : Ajoute des îlots de murs
        addWallIslands();

        // Étape 5 : Vérifie que S et E sont toujours connectés
        if (!isPathExists(1, 1, rows - 2, cols - 2)) {
            // Si S et E ne sont plus connectés, ouvre un chemin direct (sécurité)
            ensurePath(1, 1, rows - 2, cols - 2);
        }
    }

    // Ajoute des motifs complexes (croix, T, spirales)
    private void addComplexPatterns() {
        int numPatterns = random.nextInt(5) + 3; // Entre 3 et 7 motifs
        for (int p = 0; p < numPatterns; p++) {
            int patternX = random.nextInt(rows - 6) + 3; // Évite les bords
            int patternY = random.nextInt(cols - 6) + 3;
            int patternType = random.nextInt(3); // 0: croix, 1: T, 2: spirale

            switch (patternType) {
                case 0: // Motif en croix
                    if (isSafeToDrawPattern(patternX, patternY, 3, 3)) {
                        maze[patternX][patternY] = '=';
                        maze[patternX][patternY - 1] = '=';
                        maze[patternX][patternY + 1] = '=';
                        maze[patternX - 1][patternY] = '=';
                        maze[patternX + 1][patternY] = '=';
                    }
                    break;
                case 1: // Motif en T
                    if (isSafeToDrawPattern(patternX, patternY, 2, 3)) {
                        maze[patternX][patternY - 1] = '=';
                        maze[patternX][patternY] = '=';
                        maze[patternX][patternY + 1] = '=';
                        maze[patternX + 1][patternY] = '=';
                    }
                    break;
                case 2: // Motif en spirale
                    if (isSafeToDrawPattern(patternX, patternY, 4, 4)) {
                        maze[patternX][patternY] = '=';
                        maze[patternX][patternY + 1] = '=';
                        maze[patternX + 1][patternY + 1] = '=';
                        maze[patternX + 1][patternY] = '=';
                        maze[patternX + 1][patternY - 1] = '=';
                        maze[patternX + 2][patternY - 1] = '=';
                        maze[patternX + 2][patternY] = '=';
                        maze[patternX + 2][patternY + 1] = '=';
                        maze[patternX + 3][patternY + 1] = '=';
                    }
                    break;
            }
        }
    }

    // Vérifie si on peut dessiner un motif à une position donnée
    private boolean isSafeToDrawPattern(int x, int y, int height, int width) {
        if (x < 1 || y < 1 || x + height >= rows - 1 || y + width >= cols - 1) {
            return false;
        }
        return true;
    }

    // Creuse une impasse longue à partir d'une position
    private void createLongDeadEnd(int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        List<int[]> shuffledDirections = new ArrayList<>(Arrays.asList(directions));
        Collections.shuffle(shuffledDirections, random);

        for (int[] dir : shuffledDirections) {
            int newX = x, newY = y;
            int length = random.nextInt(3) + 2; // Impasse de longueur 2 à 4
            boolean canExtend = true;

            // Creuse dans une direction sur plusieurs cases
            for (int i = 0; i < length; i++) {
                newX += dir[0];
                newY += dir[1];
                if (newX <= 0 || newX >= rows - 1 || newY <= 0 || newY >= cols - 1 || maze[newX][newY] == '=') {
                    canExtend = false;
                    break;
                }
            }

            if (canExtend) {
                newX = x;
                newY = y;
                for (int i = 0; i < length; i++) {
                    newX += dir[0];
                    newY += dir[1];
                    maze[newX][newY] = '=';
                }
                break;
            }
        }
    }

    // Ajoute des îlots de murs pour forcer des détours
    private void addWallIslands() {
        int numIslands = random.nextInt(5) + 3; // Entre 3 et 7 îlots
        for (int i = 0; i < numIslands; i++) {
            int islandX = random.nextInt(rows - 4) + 2;
            int islandY = random.nextInt(cols - 4) + 2;
            if (maze[islandX][islandY] == '=' && isSafeToDrawPattern(islandX, islandY, 2, 2)) {
                maze[islandX][islandY] = '#';
                if (random.nextBoolean()) {
                    maze[islandX + 1][islandY] = '#';
                    maze[islandX][islandY + 1] = '#';
                }
            }
        }
    }

    // Vérifie si un chemin existe entre (startX, startY) et (endX, endY) avec BFS
    private boolean isPathExists(int startX, int startY, int endX, int endY) {
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];

            if (x == endX && y == endY) {
                return true;
            }

            for (int[] dir : directions) {
                int newX = x + dir[0], newY = y + dir[1];
                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && !visited[newX][newY] && maze[newX][newY] != '#') {
                    queue.add(new int[]{newX, newY});
                    visited[newX][newY] = true;
                }
            }
        }
        return false;
    }

    // Garantit un chemin entre le départ et la sortie en cas de besoin (sécurité)
    private void ensurePath(int startX, int startY, int endX, int endY) {
        int x = startX, y = startY;
        while (x != endX || y != endY) {
            if (x < endX) {
                x++;
            } else if (y < endY) {
                y++;
            }
            if (maze[x][y] != 'S' && maze[x][y] != 'E') {
                maze[x][y] = '=';
            }
        }
    }

    // Classe interne pour stocker les résultats de la résolution (chemin, étapes, temps, succès)
    public static class SolveResult {
        private final List<int[]> path; // Chemin trouvé (liste de coordonnées)
        private final int steps; // Nombre d'étapes explorées
        private final double timeMs; // Temps d'exécution en millisecondes
        private final boolean pathFound; // Indique si un chemin a été trouvé

        public SolveResult(List<int[]> path, int steps, double timeMs, boolean pathFound) {
            this.path = path;
            this.steps = steps;
            this.timeMs = timeMs;
            this.pathFound = pathFound;
        }

        public List<int[]> getPath() {
            return path;
        }

        public int getSteps() {
            return steps;
        }

        public double getTimeMs() {
            return timeMs;
        }

        public boolean isPathFound() {
            return pathFound;
        }
    }

    // Résout le labyrinthe avec l'algorithme DFS (Depth-First Search)
    public SolveResult solveDFS() {
        long startTime = System.nanoTime(); // Mesure le temps de début
        int[] steps = {0}; // Compteur d'étapes
        boolean[][] visited = new boolean[rows][cols]; // Tableau des cases visitées
        List<int[]> path = new ArrayList<>(); // Liste pour stocker le chemin
        boolean pathFound = dfs(startX, startY, visited, path, steps); // Exécute DFS
        long endTime = System.nanoTime(); // Mesure le temps de fin
        double timeMs = (endTime - startTime) / 1_000_000.0; // Calcule le temps en ms
        System.out.println("DFS: " + steps[0] + " steps, " + timeMs + " ms");
        if (pathFound) {
            int pathLength = calculatePathLength(path); // Calcule la longueur du chemin
            System.out.println("Longueur du chemin (S à E) : " + pathLength + " cases");
            printSolution(path); // Affiche la solution si un chemin est trouvé
        } else {
            System.out.println("DFS: Aucun chemin trouvé !");
        }
        return new SolveResult(path, steps[0], timeMs, pathFound);
    }

    // Algorithme DFS récursif pour trouver un chemin
    private boolean dfs(int x, int y, boolean[][] visited, List<int[]> path, int[] steps) {
        // Vérifie si la position est valide (dans les limites, non visitée, pas un mur)
        if (x < 0 || x >= rows || y < 0 || y >= cols || visited[x][y] || maze[x][y] == '#') {
            return false;
        }
        visited[x][y] = true;
        path.add(new int[]{x, y});
        steps[0]++;

        // Si la sortie est atteinte, retourne vrai
        if (x == endX && y == endY) {
            return true;
        }

        // Explore les 4 directions (droite, bas, gauche, haut)
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            if (dfs(x + dir[0], y + dir[1], visited, path, steps)) {
                return true;
            }
        }
        path.remove(path.size() - 1); // Retire la position si aucun chemin n'est trouvé
        return false;
    }

    // Résout le labyrinthe avec l'algorithme BFS (Breadth-First Search)
    public SolveResult solveBFS() {
        long startTime = System.nanoTime(); // Mesure le temps de début
        int steps = 0; // Compteur d'étapes
        Queue<int[]> queue = new LinkedList<>(); // File pour BFS
        Map<int[], int[]> parent = new HashMap<>(); // Stocke les parents pour reconstruire le chemin
        boolean[][] visited = new boolean[rows][cols]; // Tableau des cases visitées
        int[] start = {startX, startY};
        queue.add(start);
        visited[startX][startY] = true;

        // Explore les 4 directions (droite, bas, gauche, haut)
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];
            steps++;

            // Si la sortie est atteinte, reconstruit et retourne le chemin
            if (x == endX && y == endY) {
                long endTime = System.nanoTime();
                double timeMs = (endTime - startTime) / 1_000_000.0;
                System.out.println("BFS: " + steps + " steps, " + timeMs + " ms");
                List<int[]> path = reconstructPath(parent, current);
                int pathLength = calculatePathLength(path); // Calcule la longueur du chemin
                System.out.println("Longueur du chemin (S à E) : " + pathLength + " cases");
                printSolution(path);
                return new SolveResult(path, steps, timeMs, true);
            }

            // Explore les voisins
            for (int[] dir : directions) {
                int newX = x + dir[0], newY = y + dir[1];
                int[] next = {newX, newY};
                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && !visited[newX][newY] && maze[newX][newY] != '#') {
                    queue.add(next);
                    visited[newX][newY] = true;
                    parent.put(next, current);
                }
            }
        }
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("BFS: " + steps + " steps, " + timeMs + " ms");
        System.out.println("BFS: Aucun chemin trouvé !");
        return new SolveResult(new ArrayList<>(), steps, timeMs, false);
    }

    // Reconstruit le chemin à partir de la map des parents
    private List<int[]> reconstructPath(Map<int[], int[]> parent, int[] end) {
        List<int[]> path = new ArrayList<>();
        int[] current = end;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path); // Inverse le chemin pour aller de S à E
        return path;
    }

    // Calcule la longueur du chemin (nombre de cases de S à E)
    private int calculatePathLength(List<int[]> path) {
        if (path == null || path.isEmpty()) {
            return 0; // Retourne 0 si aucun chemin n'est trouvé
        }
        return path.size(); // La longueur du chemin est le nombre de cases dans le chemin
    }

    // Affiche le labyrinthe résolu dans la console avec des '+' pour le chemin
    public void printSolution(List<int[]> path) {
        char[][] solvedMaze = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            solvedMaze[i] = maze[i].clone();
        }
        for (int[] pos : path) {
            int x = pos[0], y = pos[1];
            if (solvedMaze[x][y] != 'S' && solvedMaze[x][y] != 'E') {
                solvedMaze[x][y] = '+';
            }
        }
        System.out.println("Solution:");
        for (char[] row : solvedMaze) {
            System.out.println(new String(row));
        }
        System.out.println();
    }

    // Getters pour accéder aux attributs du labyrinthe
    public char[][] getMaze() {
        return maze;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
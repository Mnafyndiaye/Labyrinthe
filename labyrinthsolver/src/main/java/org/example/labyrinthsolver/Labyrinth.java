package org.example.labyrinthsolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Labyrinth {
    private char[][] maze;
    private int rows, cols;
    private int startX, startY, endX, endY;

    public Labyrinth(char[][] maze) {
        if (maze != null) {
            this.maze = maze;
            this.rows = maze.length;
            this.cols = maze[0].length;
            findStartAndEnd();
        }
    }

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

    public void loadFromFile(String filename) {
        List<char[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.toCharArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.maze = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            this.maze[i] = lines.get(i);
        }
        this.rows = maze.length;
        this.cols = maze[0].length;
        findStartAndEnd();
    }

    public void generateRandomMaze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) {
                    maze[i][j] = '#';
                } else {
                    maze[i][j] = (Math.random() < 0.3) ? '#' : '=';
                }
            }
        }

        maze[1][1] = 'S';
        maze[rows - 2][cols - 2] = 'E';
        ensurePath(1, 1, rows - 2, cols - 2);

        findStartAndEnd();
    }

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

    // Classe pour retourner le chemin et les statistiques
    public static class SolveResult {
        private final List<int[]> path;
        private final int steps;
        private final double timeMs;
        private final boolean pathFound;

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

    public SolveResult solveDFS() {
        long startTime = System.nanoTime();
        int[] steps = {0};
        boolean[][] visited = new boolean[rows][cols];
        List<int[]> path = new ArrayList<>();
        boolean pathFound = dfs(startX, startY, visited, path, steps);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("DFS: " + steps[0] + " steps, " + timeMs + " ms");
        if (pathFound) {
            printSolution(path);
        } else {
            System.out.println("DFS: Aucun chemin trouvé !");
        }
        return new SolveResult(path, steps[0], timeMs, pathFound);
    }

    private boolean dfs(int x, int y, boolean[][] visited, List<int[]> path, int[] steps) {
        if (x < 0 || x >= rows || y < 0 || y >= cols || visited[x][y] || maze[x][y] == '#') {
            return false;
        }
        visited[x][y] = true;
        path.add(new int[]{x, y});
        steps[0]++;

        if (x == endX && y == endY) {
            return true;
        }

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            if (dfs(x + dir[0], y + dir[1], visited, path, steps)) {
                return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }

    public SolveResult solveBFS() {
        long startTime = System.nanoTime();
        int steps = 0;
        Queue<int[]> queue = new LinkedList<>();
        Map<int[], int[]> parent = new HashMap<>();
        boolean[][] visited = new boolean[rows][cols];
        int[] start = {startX, startY};
        queue.add(start);
        visited[startX][startY] = true;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];
            steps++;

            if (x == endX && y == endY) {
                long endTime = System.nanoTime();
                double timeMs = (endTime - startTime) / 1_000_000.0;
                System.out.println("BFS: " + steps + " steps, " + timeMs + " ms");
                List<int[]> path = reconstructPath(parent, current);
                printSolution(path);
                return new SolveResult(path, steps, timeMs, true);
            }

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

    private List<int[]> reconstructPath(Map<int[], int[]> parent, int[] end) {
        List<int[]> path = new ArrayList<>();
        int[] current = end;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }

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
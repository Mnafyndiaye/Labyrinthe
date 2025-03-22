module org.example.labyrinthsolver {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.labyrinthsolver to javafx.fxml;
    exports org.example.labyrinthsolver;
}
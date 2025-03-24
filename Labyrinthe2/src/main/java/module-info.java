module org.example.labyrinthe2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.labyrinthe2 to javafx.fxml;
    exports org.example.labyrinthe2;
}
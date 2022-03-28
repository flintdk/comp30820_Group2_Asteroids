package comp30820.group2.asteroids;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AsteroidsFXMLController {
    
    @FXML
    private Label label;
    
    public void initialize() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
    }    
}
module comp30820_group2_asteroids {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.media;
	
	opens comp30820.group2.asteroids to javafx.fxml;
	opens comp30820.group2.demoJFX to javafx.fxml;
	
	exports comp30820.group2.asteroids;
	exports comp30820.group2.demoJFX;
}

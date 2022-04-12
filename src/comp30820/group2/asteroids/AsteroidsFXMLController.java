package comp30820.group2.asteroids;

import java.io.IOException;
import java.net.URL;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AsteroidsFXMLController implements Initializable {

	// welcomeMenu Injectable Fields
	@FXML private TextField welcomeMenuPlayerNameEntry;
	@FXML private Label welcomeMenuPlayerNameDisplay;
	
	// mainGame Injectable Fields
	// Our canvas is magically provided by JavaFX/FXML as long, of course, as
	// the .fxml has a canvas element whose fx:id matches the name!
	@FXML private Canvas asteroidsGameCanvas;
	@FXML private Label mainGameScore;
	@FXML private Label mainGamePlayerName;
	@FXML private Label mainGameLives;
	@FXML private Label mainGameGameOver;
	
	@FXML private Label endOfGameHS1;
	@FXML private Label endOfGameHS2;
	@FXML private Label endOfGameHS3;
	@FXML private Label endOfGameHS4;
	@FXML private Label endOfGameHS5;

	// Following six labels are NOT used in the game.  However, when debugging
	// collisions, etc. it's amazingly useful to have a couple of labels to write
	// to...  so we left them here in case we decide to work on the game again.
	@FXML private Label status1;
	@FXML private Label status2;
	@FXML private Label status3;
	@FXML private Label status4;
	@FXML private Label status5;
	@FXML private Label status6;
	
	/** The initialize() method is called once on an implementing controller when
	 * the contents of its associated document have been completely loaded.  This
	 * allows the implementing class to perform any necessary post-processing on
	 * the content.
	 *
	 */
	public void initialize(URL url, ResourceBundle rb) {
//		String javaVersion = System.getProperty("java.version");
//		String javafxVersion = System.getProperty("javafx.version");
//		System.out.println("Environment: Java -> " + javaVersion + ", JavaFX -> " + javafxVersion);

		//######################################################################
		//                            WELCOME SCENE
		//######################################################################
		// If we're on the welcomeMenu screen *make sure* the welcomeMenuPlayerNameEntry
		// is in front of the display label, so the user can access it.
		if (welcomeMenuPlayerNameEntry != null) {
			welcomeMenuPlayerNameDisplay.setVisible(false);
			welcomeMenuPlayerNameEntry.toFront();
			welcomeMenuPlayerNameEntry.setVisible(true);
			GameState gameState = GameState.getInstance();
			String playerName = gameState.getPlayername();
			// If the player has already entered their name, the default it.
			// They still have to press enter... but so what...
			if (!playerName.equals("Player1")) {
				welcomeMenuPlayerNameEntry.setText(playerName);
			}
		}
		
		//######################################################################
		//                           MAIN GAME SCENE
		//######################################################################
		
		if (mainGamePlayerName != null) {
			GameState gameState = GameState.getInstance();
			String playerName = gameState.getPlayername();
			mainGamePlayerName.setText(playerName);
		}
		
		//######################################################################
		//                          END OF GAME SCENE
		//######################################################################
		
		// We only check for the existence of one of the injectable controls. If
		// it's there... we just assume we're on the endOfGame page.
		if (endOfGameHS1 != null) {
			// Copy the priority queue from the configuration (so we don't interfere
			// with the 'original one'...)
			PriorityQueue<PlayerScore> copyOfScores
				= new PriorityQueue<PlayerScore>(Configuration.HIGH_SCORES);

			endOfGameHS5.setText(copyOfScores.poll().getHallOfFameScore());
			endOfGameHS5.setVisible(true);
			endOfGameHS4.setText(copyOfScores.poll().getHallOfFameScore());
			endOfGameHS4.setVisible(true);
			endOfGameHS3.setText(copyOfScores.poll().getHallOfFameScore());
			endOfGameHS3.setVisible(true);
			endOfGameHS2.setText(copyOfScores.poll().getHallOfFameScore());
			endOfGameHS2.setVisible(true);
			endOfGameHS1.setText(copyOfScores.poll().getHallOfFameScore());
			endOfGameHS1.setVisible(true);
		}
		
		//######################################################################
		//                          HOW TO PLAY SCENE
		//######################################################################
		
		// NOTHING TO INITIALISE YET
		
	}

	//@Bryan
	// This is where the magic happens...between scenes. these methods are triggered
	// from within the FXML objects and this class is set as the controller for the
	// scenes.
	// Any queries about what anything does, drop me a line, pretty straightforward code once it works

	// We start with an ActionEvent (this method is handling an action event
	// generated by JavaFX, based on our .FXML configuration...

	// A series of .FXML anAction enpoints.  Essentially the following are a 
	// type of mapping, allowing us to connect multiple enpoints to a single
	// controller method, to avoid code duplication.
	public void asteroidsButtonClick(ActionEvent event) throws IOException {
		// Recover the stage from the event...
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		// The user has clicked a button... but which button?  We look at the id
		// of the element clicked to determine what to do...
		// ########################################################## Is there a better/proper way??
		String buttonId = ((Control)event.getSource()).getId();
		if (buttonId.equals("welcomeStartGame"))
		{
			Main.setCtrlResetGameState(true);
			activateScene(stage, Configuration.GameWindows.MAIN_GAME);
		}
		else if (buttonId.equals("welcomeHowToPlay") || buttonId.equals("endGameHowToPlay"))
		{
			activateScene(stage, Configuration.GameWindows.HOW_TO_PLAY);
		}
		else if (buttonId.equals("endGameGoToWelcomeMenu"))
		{
			activateScene(stage, Configuration.GameWindows.WELCOME_MAIN_MENU);
		}
		else if (buttonId.equals("howToPlayGoToWelcomeMenu"))
		{
			activateScene(stage, Configuration.GameWindows.WELCOME_MAIN_MENU);
		}
		else if (buttonId.equals("endGameNewGame"))
		{
				Main.setCtrlResetGameState(true);
				activateScene(stage, Configuration.GameWindows.MAIN_GAME);
		}
		else if (buttonId.equals("endGameGoToMenu")) {
			activateScene(stage, Configuration.GameWindows.WELCOME_MAIN_MENU);			
		}
		//activateScene(Configuration.GameWindows.END_OF_GAME);
	}
	
	/**
	 * @param event
	 * @throws IOException
	 */
	public void welcomeMenuPlayerNameEntryKeyPress(KeyEvent event)
	throws IOException
	{
		String playerName = welcomeMenuPlayerNameEntry.getText();

		//System.out.println(welcomeMenuPlayerNameEntry.getText());
		String keyName = event.getCode().toString();
		if (keyName == "ENTER") {
			GameState gameState = GameState.getInstance();
			gameState.setPlayername(playerName);
			
			welcomeMenuPlayerNameEntry.setVisible(false);
			welcomeMenuPlayerNameDisplay.setText("WELCOME " + playerName.toString());
			welcomeMenuPlayerNameDisplay.setVisible(true);
						
		}

	}
	
	// We need access to the stage for changing scenes etc..
	// Our application only has one stage (window) and this will never change.
	// The stage (generated at application launch) is our main game window.
	// **********   Not sure if this is the best approach?? ???????

	protected void activateScene(Stage stage, Configuration.GameWindows window)
	throws IOException
	{
		//root = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
		//stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		//scene = new Scene(root);
		//stage.setScene(scene);
		//stage.show();
		
		// Code for access to everything relating to JavaFX.  This might not be
		// necessary - but for an abundance of caution...

		// @Bryan
		// Uncomment below to see the 3 screens directly, Both home and end menu
		// have buttons for switching but need to fix the game screen, issue with
		// ship and adding clickable feature

		//FXMLLoader loader = new FXMLLoader(getClass().getResource("asteroidsBorderPane.fxml"));
		FXMLLoader loader = new FXMLLoader(getClass().getResource(window.fxmlResource));
		//FXMLLoader loader = new FXMLLoader(getClass().getResource("endmenu.fxml"));

		Parent root = (Parent) loader.load();

		// Following line included just in case we need access to the controller...
		//AsteroidsFXMLController controller = loader.<AsteroidsFXMLController>getController();

		// If we're displaying a menu page we just chuck a new scene onto the stage,
		// but if we're going to the mainGame then we're careful to reuse the mainGameScene
		// which is created on application startup.
		Scene scene;
		if (window == Configuration.GameWindows.MAIN_GAME) {
			scene = Main.getMainGameScene();
		}
		else {
			// Then we create a scene based on the FXML root...
			scene = new Scene(root);
		}
		KeyStrokeManager.getInstance().manageThisScene(scene);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		// Each stage has a scene (or scenes) and each scene requires a layout
		// manager.  The JavaFX SDK provides several layout panes for the easy
		// setup and management of classic layouts such as rows, columns, stacks,
		// tiles, and others.  Read more about the various options here:
		//   -> https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm

		// Next we attach this scene to the stage (ours is 'stage'...)
		stage.setScene(scene);
		stage.show();
	}

	// Getters and Setters

	public Canvas getAsteroidsGameCanvas() {
		return asteroidsGameCanvas;
	}
	public void setAsteroidsGameCanvas(Canvas asteroidsGameCanvas) {
		this.asteroidsGameCanvas = asteroidsGameCanvas;
	}

}
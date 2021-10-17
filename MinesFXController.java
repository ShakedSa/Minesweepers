package mines;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MinesFXController {

	@FXML
	private BorderPane root;

	@FXML
	private Button resetBtn;

	@FXML
	private TextField heightField;

	@FXML
	private TextField minesField;

	@FXML
	private TextField widthField;

	@FXML
	private StackPane gridHolder;

	@FXML
	private Label timer;

	@FXML
	private Label gridWidth;

	@FXML
	private Label gridHeight;

	@FXML
	private Label minesAmount;

	@FXML
	private Label totalFlags;
	private int countMines;

	// Timer properties
	/*----------------*/
	private long count;
	private boolean startTimer = false;
	private Timer myTimer;
	private TimerTask timerTask;

	public boolean getStarted() {
		/* For the main thread, when closed check if the timer is still running. */
		return startTimer;
	}

	public Timer getTimer() {
		return myTimer;
	}
	/*----------------*/

	/* Regular expression to check if a given string is numeric. */
	/*----------------*/
	private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	private boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		if (pattern.matcher(str).matches()) {
			return true;
		}
		return false;
	}
	/*----------------*/

	// Get Stage
	/*----------------*/
	private Stage stage;

	public void setStage(Stage stage) {
		/*
		 * Saving the instance of the stage to resize it whenever starting a new game.
		 */
		this.stage = stage;
	}

	/*----------------*/
	@FXML
	void ResetMines(ActionEvent event) {
		resetBtn.requestFocus(); // Setting focus on the reset button.
		int height, width, minesNum;
		final Alert a = new Alert(AlertType.ERROR);
		boolean isOk = true;
		height = width = minesNum = 0;
		/*
		 * Checking if all the field were initialized with a numeric value. if not give
		 * a default value of 0 and alert the user.
		 */
		if (!isNumeric(heightField.getText())) {
			isOk = false;
		} else {
			height = Integer.parseInt(heightField.getText());
		}
		if (!isNumeric(widthField.getText())) {
			isOk = false;
		} else {
			width = Integer.parseInt(widthField.getText());
		}
		if (!isNumeric(minesField.getText())) {
			isOk = false;
		} else {
			minesNum = Integer.parseInt(minesField.getText());
		}
		if (!isOk) {
			/* If one or more values are not numeric alert and return. */
			a.setHeaderText("One or more values are not numeric.");
			a.show();
			return;
		}
		countMines = minesNum;
		timerTask = new TimerTask() {
			/*
			 * Setting a timer task to check how much time it takes to finish the game. if a
			 * sole purpose is to improve your time.(The times are not saved in a
			 * "board score".
			 */
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						count++;
						timer.setText(String.format("Timer: %02d:%02d", count / 60, count % 60));
					}
				});
			}
		};
		if (startTimer) {
			/* If the timer is already started cancel it. */
			myTimer.cancel();
		}
		myTimer = new Timer(); // Starts a new timer.
		count = 0;
		timer.setText(String.format("Timer: %02d:%02d", count / 60, count % 60));
		myTimer.schedule(timerTask, 0, 1000); // Setting a timer task that runs every 1 second(1000ms)
		startTimer = true;
		final Image startImage = new Image("file:src/mines/images/start.png");
		final ImageView startImageShow = new ImageView(startImage); // Setting the starting image(A happy smiley face)
		startImageShow.setFitHeight(20);
		startImageShow.setFitWidth(20);
		resetBtn.setGraphic(startImageShow);
		FadeIn(); // Creating a light fade-in for the grid display.
		GridPane mineGrid = buildMinesGrid(height, width, minesNum);
		gridHolder.getChildren().clear(); // Removing the grid of the previous game.
		gridHolder.getChildren().add(mineGrid);
		gridHolder.setPrefHeight(mineGrid.getHeight());
		gridHolder.setPrefWidth(mineGrid.getWidth());
		totalFlags.setText("Mines: " + minesNum);
		stage.sizeToScene(); // Setting the stage to a fixed size according to the scene.
	}

	/* A method for building the grid. */
	private GridPane buildMinesGrid(int height, int width, int minesNum) {
		final GridPane mineGrid = new GridPane();
		final Mines mines = new Mines(height, width, minesNum); // A Mines object to create the functionality of the
																// game.
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				final MinesButton btn = new MinesButton(i, j); // Creating a new MinesButton.
				btn.setText(mines.get(i, j));
				btn.setTextAlignment(TextAlignment.CENTER); // Setting text alignment for the button.
				btn.setAlignment(Pos.CENTER);
				btn.setStyle("-fx-font-family: 'Trebuchet MS', 'Lucida Sans Unicode'," + ""
						+ "'Lucida Grande', 'Lucida Sans', Arial, sans-serif;" + "-fx-font-weight: bold;"
						+ "-fx-font-style: italic;" + "-fx-font-size: 12"); // Setting a style for the button.
				btn.setMinWidth(30);
				btn.setMinHeight(30);
				btn.setMaxHeight(30);
				btn.setMaxWidth(30);
				btn.setCursor(Cursor.HAND); // Changing the cursor on hover to indicate that it's a clickable button.
				mineGrid.add(btn, j, i);
				btn.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					/*
					 * OnClickEvent - waiting for a mouse click event on the button, and opening the
					 * button accordingly using the Mines object.
					 */
					@Override
					public void handle(MouseEvent event) {
						resetBtn.requestFocus();
						// Right click
						/*----------------*/
						if (event.getButton() == MouseButton.SECONDARY) {
							/*
							 * If a right-click happens toggle the flag, and indicate that there are less
							 * mines. Like in the real game, just indication of how many flags put in on the
							 * field not actually how many flags were put on a mine.
							 */
							mines.toggleFlag(btn.getX(), btn.getY());
							if (mines.get(btn.getX(), btn.getY()).equals("F")) {
								countMines--;
							} else {
								countMines++;
							}
							totalFlags.setText("Mines: " + countMines);
							renderGrid(mines, mineGrid, height, width);
						}
						// Left click
						/*----------------*/
						else {
							final int x = btn.getX();
							final int y = btn.getY();
							final Alert a = new Alert(null);
							boolean alert = false;
							/*
							 * Opening the mines from the clicked button. if the open method returns false
							 * --> we tried to open a mine and lost the game.
							 */
							if (!mines.open(x, y)) {
								/* Setting the alert content for display on a lose. */
								a.setHeaderText(String.format("Landed on a mine!\nTotal time: %02d:%02d", count / 60,
										count % 60));
								Image lostImage = new Image("file:src/mines/images/lose.png");
								ImageView lostImageShow = new ImageView(lostImage);
								lostImageShow.setFitHeight(20);
								lostImageShow.setFitWidth(20);
								resetBtn.setGraphic(lostImageShow);
								/* Creating a fade-out-in of the grid on a lose. */
								winLoseFade();
								mines.setShowAll(true); // When rendering the grid after the button click, display all
														// the border.
								alert = true;
								myTimer.cancel(); // Closing the timer(the game ended)
								startTimer = false; // Indicating that there is no active timer.
								totalFlags.setText("Mines: " + minesNum);

							} else {
								/*
								 * If we didn't land on a mine, check to see if this was the last openable
								 * button that is not a mine on the board. meaning you won.
								 */
								if (mines.isDone()) {
									/* Setting the alert content for display on a win. */
									a.setHeaderText(String.format("The board is clear!\nTotal time: %02d:%02d",
											count / 60, count % 60));
									Image wonImage = new Image("file:src/mines/images/win.png");
									ImageView winImageShow = new ImageView(wonImage);
									winImageShow.setFitHeight(20);
									winImageShow.setFitWidth(20);
									resetBtn.setGraphic(winImageShow);
									/* Creating a fade-out-in of the grid on a win. */
									winLoseFade();
									alert = true;
									myTimer.cancel(); // Closing the timer(the game ended)
									startTimer = false; // Indicating that there is no active timer.
									totalFlags.setText("Mines: " + minesNum);
								}
							}
							/* After every button press re-render the grid to display the changes */
							renderGrid(mines, mineGrid, height, width);
							if (alert) {
								/*
								 * If the game ended(win or lose), display the alert and disable the
								 * grid's(won't be pressable).
								 */
								mineGrid.setDisable(true);
								a.setAlertType(AlertType.INFORMATION);
								a.setTitle("Mines Sweepers");
								a.setGraphic(null);
								a.show();
								resetBtn.requestFocus();
							}
						}
					}
				});
			}
		}
		return mineGrid;
	}

	// Fade in out.
	/*--------------------*/
	private void FadeIn() {
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(2000));
		fadeTransition.setNode(gridHolder);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);
		fadeTransition.play();
	}

	/* A bit more dramatic fade to indicate that the game is over (win or lose). */
	private void winLoseFade() {
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(2500));
		fadeTransition.setNode(gridHolder);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.setFromValue(0);
		fadeTransition.setToValue(1);
		fadeTransition.play();
	}
	/*--------------------*/

	/* After each button open, re-render the grid to display the difference. */
	private void renderGrid(Mines mines, GridPane mineGrid, int height, int width) {
		for (int k = 0; k < height; k++) {
			for (int v = 0; v < width; v++) {
				final MinesButton mBtn;
				mBtn = (MinesButton) mineGrid.getChildren().get(v * height + k);
				final String getString = mines.get(mBtn.getX(), mBtn.getY());
				mBtn.setText(getString);
				mBtn.setFocusTraversable(false);
				/* Setting a color/image according to the place's state. */
				if (!getString.equals(".")) {
					switch (getString) {
					case "1":
						mBtn.setTextFill(Color.BLUE);
						break;
					case "2":
						mBtn.setTextFill(Color.GREEN);
						break;
					case "3":
						mBtn.setTextFill(Color.RED);
						break;
					case "4":
						mBtn.setTextFill(Color.DARKBLUE);
						break;
					case "5":
						mBtn.setTextFill(Color.BROWN);
						break;
					case "X":
						mBtn.setText("");
						Image mine = new Image("file:src/mines/images/mine.jpg");
						ImageView mineShow = new ImageView(mine);
						mineShow.setFitHeight(20);
						mineShow.setFitWidth(20);
						mBtn.setGraphic(mineShow);
						mineShow.autosize();
						mBtn.setTextFill(Color.BLACK);
						break;
					case "F":
						mBtn.setText("");
						Image flag = new Image("file:src/mines/images/flag_icon.png");
						ImageView flagShow = new ImageView(flag);
						flagShow.setFitHeight(20);
						flagShow.setFitWidth(20);
						mBtn.setGraphic(flagShow);
						mBtn.setTextFill(Color.PURPLE);
						break;
					default:
						mBtn.setTextFill(Color.DARKCYAN);
					}
					if (!getString.equals("F") && !getString.equals("X")) {
						/* If the flag was toggled on, remove the image. */
						if (mBtn.getGraphic() != null) {
							countMines++;
							totalFlags.setText("Mines: " + countMines);
						}
						mBtn.setGraphic(null);
						mBtn.setDisable(true); // Disable the button, so it won't be clickable.
					}
				} else {
					mBtn.setGraphic(null);
					mBtn.setTextFill(Color.BLACK);
				}
			}
		}
	}

	// Setting style for labels, cursor on reset button and setting focus onload on
	// the first text field
	/*----------------*/
	public void setStyle() {
		gridHeight.setStyle("-fx-font-family: 'Trebuchet MS', 'Lucida Sans Unicode'," + ""
				+ "'Lucida Grande', 'Lucida Sans', Arial, sans-serif;" + "-fx-font-weight: bold;"
				+ "-fx-font-style: italic;");
		gridWidth.setStyle("-fx-font-family: 'Trebuchet MS', 'Lucida Sans Unicode'," + ""
				+ "'Lucida Grande', 'Lucida Sans', Arial, sans-serif;" + "-fx-font-weight: bold;"
				+ "-fx-font-style: italic;");
		minesAmount.setStyle("-fx-font-family: 'Trebuchet MS', 'Lucida Sans Unicode'," + ""
				+ "'Lucida Grande', 'Lucida Sans', Arial, sans-serif;" + "-fx-font-weight: bold;"
				+ "-fx-font-style: italic;");
		timer.setStyle("-fx-font-family: 'Trebuchet MS', 'Lucida Sans Unicode'," + ""
				+ "'Lucida Grande', 'Lucida Sans', Arial, sans-serif;" + "-fx-font-weight: bold;"
				+ "-fx-font-style: italic;");
		totalFlags.setStyle("-fx-font-family: 'Trebuchet MS', 'Lucida Sans Unicode'," + ""
				+ "'Lucida Grande', 'Lucida Sans', Arial, sans-serif;" + "-fx-font-weight: bold;"
				+ "-fx-font-style: italic;");
		resetBtn.setCursor(Cursor.HAND);
		resetBtn.setFocusTraversable(true);
		widthField.requestFocus();
	}
	/*----------------*/
}

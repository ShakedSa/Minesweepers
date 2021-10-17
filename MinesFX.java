package mines;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MinesFX extends Application{
	
	@Override
	public void start(Stage stage) {
		final BorderPane  board;
		final MinesFXController controller;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("MinesFX.fxml"));
			board = loader.load();
			controller = loader.getController();
		}catch(Exception e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(board);
		controller.setStyle();
		controller.setStage(stage);
		stage.setScene(scene);
		stage.setTitle("Mines Sweepers");
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			/*On application close, if the timer is still running, close it.*/
			@Override
			public void handle(WindowEvent e) {
				if(controller.getStarted()) {
					controller.getTimer().cancel();
				}
			}
		});
	}
	public static void main(String[] args) {
		launch(args);
	}
}

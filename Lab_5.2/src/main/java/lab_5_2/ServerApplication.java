package lab_5_2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lab_5_2.server.ServerController;

public class ServerApplication
		extends Application
{

	@Override
	public void start(Stage primaryStage)
	throws Exception {
		var fxmlLoader = new FXMLLoader(ServerController.class.getResource("server-view.fxml"));
		var scene = new Scene(fxmlLoader.load());

		primaryStage.setTitle("Serwer");
		primaryStage.setScene(scene);
		primaryStage.show();

		var controller = (ServerController)fxmlLoader.getController();
		controller.binding_initialize();

		primaryStage.setOnCloseRequest(e -> controller.stopMenuItem_onAction(null));
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}

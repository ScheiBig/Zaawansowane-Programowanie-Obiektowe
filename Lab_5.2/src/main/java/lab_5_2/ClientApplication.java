package lab_5_2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lab_5_2.client.ClientController;

public class ClientApplication
		extends Application
{

	@Override
	public void start(Stage primaryStage)
	throws Exception {
		var fxmlLoader = new FXMLLoader(ClientController.class.getResource("client-view.fxml"));
		var scene = new Scene(fxmlLoader.load());

		primaryStage.setTitle("Klient");
		primaryStage.setScene(scene);
		primaryStage.show();

		var controller = (ClientController) fxmlLoader.getController();
		controller.binding_initialize();

		primaryStage.setOnCloseRequest(e -> controller.disconnectionMenuItem_onAction(null));
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}

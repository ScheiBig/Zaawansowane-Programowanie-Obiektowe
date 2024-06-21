package lab_5_2.server;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lab_5_2.Config;
import org.jetbrains.annotations.Nullable;
import utilx.result.Res;

import java.net.Socket;

public class ServerController {
	@FXML protected MenuItem launchMenuItem;
	@FXML protected BorderPane circleContainerBorderPane;
	@FXML protected Circle circle;
	@FXML protected HBox statusBar;
	@FXML protected Label networkLabel;
	@FXML protected MenuItem stopMenuItem;

	private final Double slider_initialValue = 0.3;
	private DoubleProperty cacheSlider;

	private @Nullable Socket socket;
	private Server_CommunicationTask communicationTask;

	@FXML
	protected void initialize() {
		this.circle.setFill(Color.GRAY);
	}

	/**
	 * This method is necessary to create bindings for circle radius, that wil have actually
	 * meaningful initial values.
	 * <p>
	 * It must be called <b>after</b> stage containing this view is shown.
	 */
	public void binding_initialize() {

		var paneMinDimension = Bindings.min(this.circleContainerBorderPane.heightProperty(),
				this.circleContainerBorderPane.widthProperty()
		);

		this.cacheSlider = new SimpleDoubleProperty(this.slider_initialValue);

		var newRadius = paneMinDimension.multiply(this.cacheSlider)
				.multiply(0.5);

		this.circle.radiusProperty()
				.bind(newRadius);
	}

	@FXML
	protected void launchMenuItem_onAction(ActionEvent event) {

		var task = new Server_SocketConnectTask(Config.Port);

		task.valueProperty()
				.addListener((obs, oldVal, val) -> {
					switch (val) {
						case Success -> {
							this.launchMenuItem.setVisible(false);
							this.stopMenuItem.setVisible(true);
							this.networkLabel.setText("Serwer uruchomiony: połączony z klientem " +
									"@" +
									" " +
									val.socket.getInetAddress()
											.getHostAddress() +
									":" +
									val.socket.getPort());
							this.circle.setFill(Color.BLUE);
							this.socket = val.socket;
							this.startClientCommunication();
						}
						case SS_IOError, SS_SecurityError, CS_IOError, CS_SecurityError -> {
							this.launchMenuItem.setVisible(true);
							this.stopMenuItem.setVisible(false);
							this.launchMenuItem.setDisable(false);
							this.networkLabel.setText("Serwer nie uruchomiony: błąd uruchamiania");
							this.circle.setFill(Color.GRAY);
							this.socket = null;
							Config.debugPrintError(val.error);
							var errorAlert = new Alert(Alert.AlertType.ERROR);
							errorAlert.setTitle("Błąd uruchamiania serwera");
							errorAlert.setHeaderText(val.toString());
							errorAlert.setContentText(Config.prettyPrint(val.error));
							errorAlert.showAndWait();
						}
						default -> {
						}
					}
				});

		var thread = new Thread(task);
		thread.setName("Server_SocketConnectTask");
		thread.start();
		this.launchMenuItem.setDisable(true);

		this.circle.setFill(Color.GRAY);
		this.networkLabel.setText("Serwer uruchomiony: oczekiwanie na klienta");
	}

	protected void updateSteering(Double value) {
		if (value == null) {
			return;
		}
		Platform.runLater(() -> {
			this.cacheSlider.set(value);
		});
	}

	@SuppressWarnings("CodeBlock2Expr")
	protected void startClientCommunication() {
		var task_res = Res.from(() -> {
			return new Server_CommunicationTask(this.socket, this::updateSteering);
		});

		if (task_res.err() != null) {
			Config.debugPrintError(task_res.err());
			var errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Błąd łączenia klienta");
			errorAlert.setHeaderText("Błąd I/O podczas inicjowania komunikacji z klientem");
			errorAlert.setContentText(Config.prettyPrint(task_res.err()));
			errorAlert.showAndWait();

			this.launchMenuItem_onAction(null);
		}

		this.communicationTask = task_res.val();
		this.communicationTask.valueProperty()
				.addListener((osb, oldVal, val) -> {
					if (val == null) {
						return;
					}
					Config.debugPrintError(val.error);
					var errorAlert = new Alert(Alert.AlertType.ERROR);
					if (val == Server_CommunicationTask.Result.RO_DisconnectWarning) {
						errorAlert.setAlertType(Alert.AlertType.WARNING);
					}
					errorAlert.setTitle("Błąd komunikacji klienta");
					errorAlert.setHeaderText(val.toString());
					errorAlert.setContentText(Config.prettyPrint(val.error));
					errorAlert.showAndWait();

					this.launchMenuItem_onAction(null);
				});

		var thread = new Thread(this.communicationTask);
		thread.setName("Server_CommunicationTask");
		thread.start();
	}

	public void stopMenuItem_onAction(ActionEvent event) {
		if (this.communicationTask != null) {
			this.communicationTask.cancel();
		}
		Res.from(() -> this.socket.close());
		this.launchMenuItem.setVisible(true);
		this.stopMenuItem.setVisible(false);
		this.launchMenuItem.setDisable(false);
		this.networkLabel.setText("Serwer nie uruchomiony");
		this.circle.setFill(Color.GRAY);
	}
}

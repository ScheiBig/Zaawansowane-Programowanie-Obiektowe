package lab_5_2.client;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lab_5_2.Config;
import org.jetbrains.annotations.Nullable;
import utilx.result.Res;

import java.net.Socket;

public class ClientController {

	@FXML protected VBox vBox;
	@FXML protected MenuItem connectionMenuItem;
	@FXML protected CheckMenuItem steerCheckMenuItem;
	@FXML protected Slider slider;
	@FXML protected BorderPane circleContainerBorderPane;
	@FXML protected Circle circle;
	@FXML protected HBox statusBar;
	@FXML protected Label networkLabel;
	@FXML protected MenuItem disconnectionMenuItem;

	private final Double slider_initialValue = 0.3;
	private DoubleProperty cacheSlider;

	private @Nullable Socket socket;
	private Client_CommunicationTask communicationTask;

	@FXML
	protected void initialize() {
		var greenWhenSteer = Bindings.when(this.steerCheckMenuItem.selectedProperty())
				.then(Color.GREEN)
				.otherwise(Color.GRAY);

		this.circle.fillProperty()
				.bind(greenWhenSteer);

		this.slider.setValue(this.slider_initialValue);
	}

	/**
	 * This method is necessary to create bindings for circle radius, that wil have actually
	 * meaningful initial values.
	 * <p>
	 * It must be called <b>after</b> stage containing this view is shown.
	 */
	public void binding_initialize() {

		var paneMinDimension = Bindings.min(
				this.circleContainerBorderPane.heightProperty(),
				this.circleContainerBorderPane.widthProperty()
		);

		this.cacheSlider = new SimpleDoubleProperty(this.slider_initialValue);

		this.slider.valueProperty()
				.addListener((obs, oldVal, val) -> {
					if (this.steerCheckMenuItem.isSelected()) {
						this.cacheSlider.set(val.doubleValue());
						if (this.communicationTask != null) {
							this.communicationTask.add(val.doubleValue());
						}
					}
				});

		var newRadius = paneMinDimension.multiply(this.cacheSlider)
				.multiply(0.5);


		this.circle.radiusProperty()
				.bind(newRadius);
	}

	@FXML
	protected void connectionMenuItem_onAction(ActionEvent event) {
		var connectAlert = new Alert(Alert.AlertType.INFORMATION, null,
				// Hack to get Cancel button, but get its click as positive value
				new ButtonType("Anuluj", ButtonBar.ButtonData.OK_DONE)
		);

		var task = new Client_SocketConnectTask(
				Config.Host,
				Config.Port,
				Config.ClientConnectTimeout_ms
		);

		task.valueProperty()
				.addListener((obs, oldVal, val) -> {
					Platform.runLater(() -> {
						//noinspection Convert2MethodRef
						connectAlert.close();
					});
				});

		var thread = new Thread(task);
		thread.setName("Client_SocketConnectTask");
		thread.start();

		connectAlert.setTitle("Łączenie");
		connectAlert.setHeaderText("Trwa łączenie z serwerem...");
		connectAlert.showAndWait();

		if (!task.isDone()) {
			this.connectionMenuItem.setVisible(true);
			this.disconnectionMenuItem.setVisible(false);
			this.networkLabel.setText("Klient nie podłączony: przerwano próbę łączenia");
			task.cancel();
		} else {
			var val = task.getValue();
			switch (val) {
				case Success -> {
					this.connectionMenuItem.setVisible(false);
					this.disconnectionMenuItem.setVisible(true);
					this.networkLabel.setText("Połączono z serwerem @ " +
							val.socket.getInetAddress()
									.getHostAddress() +
							":" +
							val.socket.getPort());
					this.socket = val.socket;
					this.startServerCommunication();
				}
				case TimeoutError, IOError -> {
					this.connectionMenuItem.setVisible(true);
					this.disconnectionMenuItem.setVisible(false);
					this.networkLabel.setText("Klient nie podłączony: błąd łączenia");
					this.socket = null;

					Config.debugPrintError(val.error);
					var errorAlert = new Alert(Alert.AlertType.ERROR);
					errorAlert.setTitle("Błąd łączenia");
					errorAlert.setHeaderText(val.toString());
					errorAlert.setContentText(Config.prettyPrint(val.error));
					errorAlert.showAndWait();
				}
				default -> {
				}
			}
		}
	}

	@SuppressWarnings("CodeBlock2Expr")
	protected void startServerCommunication() {
		var task_res = Res.from(() -> {
			return new Client_CommunicationTask(this.socket, Config.CommunicationDebounce_ms);
		});

		if (task_res.err() != null) {
			this.connectionMenuItem.setVisible(true);
			this.disconnectionMenuItem.setVisible(false);
			this.networkLabel.setText("Klient nie podłączony: błąd połączenia");
			Config.debugPrintError(task_res.err());
			var errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Błąd łączenia z serwerem");
			errorAlert.setHeaderText("Błąd I/O podczas inicjowania komunikacji z serwerem");
			errorAlert.setContentText(Config.prettyPrint(task_res.err()));
			errorAlert.showAndWait();
		}

		this.communicationTask = task_res.val();
		this.communicationTask.valueProperty()
				.addListener((osb, oldVal, val) -> {
					if (val == null) {
						return;
					}
					this.connectionMenuItem.setVisible(true);
					this.disconnectionMenuItem.setVisible(false);
					this.networkLabel.setText("Klient nie podłączony: błąd połączenia");
					Config.debugPrintError(val.error);
					var errorAlert = new Alert(Alert.AlertType.ERROR);
					errorAlert.setTitle("Błąd komunikacji serwera");
					errorAlert.setHeaderText(val.toString());
					errorAlert.setContentText(Config.prettyPrint(val.error));
					errorAlert.showAndWait();
					this.communicationTask = null;
				});

		var thread = new Thread(this.communicationTask);
		thread.setName("Client_CommunicationTask");
		thread.start();
	}

	public void disconnectionMenuItem_onAction(ActionEvent event) {
		if (this.communicationTask != null) {
			this.communicationTask.cancel();
		}
		this.connectionMenuItem.setVisible(true);
		this.disconnectionMenuItem.setVisible(false);
		this.networkLabel.setText("Klient nie podłączony");
	}
}

package lab_5_2;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class HelloController {
	@FXML
	private Label welcomeText;

	@FXML
	private Slider slider;

	@FXML
	private Button helloButton;

	@FXML
	public VBox vBox;

	@FXML
	protected void onHelloButtonClick() {
		welcomeText.setText("Welcome to JavaFX Application!");
	}

	@FXML
	protected void initialize() {

		helloButton.prefWidthProperty()
				.bind(slider.valueProperty()
						.multiply(vBox.widthProperty()
								.subtract(vBox.spacingProperty())));


	}
}
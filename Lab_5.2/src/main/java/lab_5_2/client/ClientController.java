package lab_5_2.client;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ClientController {
	@FXML protected VBox vBox;

	@FXML protected CheckMenuItem steer;

	@FXML protected Slider slider;

	@FXML protected BorderPane pane;

	@FXML protected Circle circle;

	private DoubleProperty cacheRadius;

	@FXML
	protected void initialize() {
		var greenWhenSteer = Bindings.when(steer.selectedProperty())
				.then(Color.GREEN)
				.otherwise(Color.GRAY);

		circle.fillProperty()
				.bind(greenWhenSteer);
	}

	/**
	 * This method is necessary to create bindings for circle radius, that wil have actually
	 * meaningful initial values.
	 */
	public void launch() {

		var paneMinDimension = Bindings.min(pane.heightProperty(), pane.widthProperty())
				.multiply(slider.valueProperty())
				.multiply(0.5);

		cacheRadius = new SimpleDoubleProperty(paneMinDimension.doubleValue());

		circle.radiusProperty()
				.addListener((obs, oldVal, val) -> cacheRadius.set(val.doubleValue()));

		var steerCircleRadius = Bindings.when(steer.selectedProperty())
				.then(paneMinDimension)
				.otherwise(cacheRadius);

		circle.radiusProperty()
				.bind(steerCircleRadius);
	}
}

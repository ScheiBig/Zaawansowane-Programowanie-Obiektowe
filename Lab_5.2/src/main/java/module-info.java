module lab_5_2 {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.jetbrains.annotations;


	opens lab_5_2 to javafx.fxml;
	opens lab_5_2.client to javafx.fxml;
	exports lab_5_2;
}
module lab_5_2 {
	requires javafx.controls;
	requires javafx.fxml;


	opens lab_5_2 to javafx.fxml;
	exports lab_5_2;
}
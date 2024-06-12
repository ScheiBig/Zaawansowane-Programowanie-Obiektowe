module lab_4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    requires org.controlsfx.controls;
	requires java.desktop;

	opens lab_4 to javafx.fxml;
    exports lab_4;
}

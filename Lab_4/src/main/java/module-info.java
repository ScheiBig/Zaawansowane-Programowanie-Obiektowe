module edu.jeznach.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    requires org.controlsfx.controls;

    opens lab_4 to javafx.fxml;
    exports lab_4;
}
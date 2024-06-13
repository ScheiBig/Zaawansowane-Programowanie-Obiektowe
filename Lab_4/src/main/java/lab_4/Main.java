package lab_4;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.TwoWayMonitor;
import lab_4.io.BufferingMode;
import lab_4.io.ReaderThread;
import lab_4.io.WriterThread;
import lab_4.javafx.utils.FX;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main
		extends Application
{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage)
	throws URISyntaxException, IOException, InterruptedException {
		var button = new Button[1];
		var comboBox = new ComboBox[1];

		stage.setScene(FX.scene(
				FX.vBox((vb, vb_ch) -> {

					vb_ch.add(FX.region(r -> VBox.setVgrow(r, Priority.ALWAYS)));
					vb_ch.add(FX.label(l -> {
						l.setText("""
								Okno główne aplikacji
								Zamknięcie tego okna spowoduje bezpieczne zamknięcie aplikacji
								Wybierz tryb programu, i uruchom:""");
						l.setAlignment(Pos.CENTER);
						l.setTextAlignment(TextAlignment.CENTER);
						l.setMaxWidth(Double.MAX_VALUE);
					}));
					vb_ch.add(FX.region(r -> r.setPrefHeight(10.0)));
					vb_ch.add(FX.hBox((hb, hb_ch) -> {
						hb_ch.add(FX.region(r -> HBox.setHgrow(r, Priority.ALWAYS)));
						hb_ch.add(FX.comboBox(cb -> {
							cb.setItems(FXCollections.observableArrayList(BufferingMode.values()));
							cb.setValue(BufferingMode.ByChar);
							comboBox[0] = cb;
						}));
						hb_ch.add(FX.region(r -> r.setPrefWidth(20.0)));
						hb_ch.add(FX.button(b -> {
							b.setText("Uruchom");
							button[0] = b;
						}));
						hb_ch.add(FX.region(r -> HBox.setHgrow(r, Priority.ALWAYS)));
					}));
					vb_ch.add(FX.region(r -> VBox.setVgrow(r, Priority.ALWAYS)));

				}),
				FX.NodeInitializer::noInit
		));
		stage.setTitle("Zadanie 1");
		stage.show();
		stage.setX(20);
		stage.setY(20);
		stage.setWidth(380);
		stage.setHeight(240);
		stage.setAlwaysOnTop(true);

		var waitGroup = new Semaphore(Config.Files.size(), true);
		var monitor = new TwoWayMonitor();
		var buffer = new Character[1];
		var bufferOwnership = new Semaphore(1, true);

		var out = new WriterThread(
				Files.newBufferedWriter(Path.of(Config.Result.toURI())),
				Path.of(Config.Result.toURI())
						.getFileName()
						.toString(),
				buffer,
				monitor,
				waitGroup
		);
		out.window.setX(20 + stage.getWidth() + 20);
		out.window.setY(20);
		out.window.show();
		out.window.setAlwaysOnTop(true);

		var readers = new ArrayList<ReaderThread>();

		for (int i = 0; i < Config.Files.size(); i++) {
			var f = Config.Files.get(i);
			try {
				var rt = new ReaderThread(
						Files.newBufferedReader(Path.of(f.toURI())),
						Path.of(f.toURI())
								.getFileName()
								.toString(),
						buffer,
						monitor,
						waitGroup,
						bufferOwnership,
						BufferingMode.ByChar
				);
				readers.add(rt);
				rt.window.show();
				rt.window.setX(20 +
						stage.getWidth() +
						20 +
						out.window.getWidth() +
						20 +
						(rt.window.getWidth() + 20) * i);
				rt.window.setY(20);
				rt.window.setAlwaysOnTop(true);

			} catch (InterruptedException | URISyntaxException | IOException e) {
				throw new RuntimeException(e);
			}
		}

		stage.setOnCloseRequest(e -> {
			out.interrupt();
			readers.forEach(Thread::interrupt);
			System.exit(0);
		});

		button[0].setOnMouseClicked(e -> {
			readers.forEach(r -> r.setBufferingMode((BufferingMode) comboBox[0].getValue()));
			readers.forEach(Thread::start);
			out.start();
			button[0].setOnMouseClicked(Event::consume);
			button[0].setDisable(true);
			comboBox[0].setDisable(true);
		});

		System.out.println(this.getParameters()
				.getRaw());
	}
}

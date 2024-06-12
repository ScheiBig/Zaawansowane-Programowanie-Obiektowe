package lab_4;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.TwoWayMonitor;
import lab_4.io.BufferingMode;
import lab_4.io.ReaderThread;
import lab_4.io.WriterThread;

public class Main
		extends Application
{

	public static final List<URL> pliki;
	public static final URL wynik;

	static {
		try {
			pliki = new ArrayList<>();
			Arrays.stream(new File("src/main/resources/in2").listFiles())
					.map(File::toURI)
					.map(uri -> {
						try {
							return uri.toURL();
						} catch (MalformedURLException e) {
							throw new RuntimeException(e);
						}
					})
					.forEach(pliki::add);
			wynik = Path.of("src/main/resources/wynik.txt")
					.toUri()
					.toURL();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final long ASSUME_THREAD_DEAD__MS = 25;
	public static final long BOUNCE__MS = 10;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage)
	throws URISyntaxException, IOException, InterruptedException {
		var vbox = new VBox();
		var hbox = new HBox();
		var label = new Label();
		var scene = new Scene(vbox, 360, 240);
		var options =
				FXCollections.observableArrayList(BufferingMode.values());
		var comboBox = new ComboBox<>(options);
		var button = new Button("Uruchom");

		stage.setTitle("Zadanie 1");
		stage.setScene(scene);
		stage.show();
		stage.setX(20);
		stage.setY(20);
		stage.setAlwaysOnTop(true);

		label.setText("Okno główne aplikacji\n" + "Zamknięcie tego okna spowoduje bezpieczne " +
				"zamknięcie aplikacji\n" + "Wybierz tryb programu, i uruchom:");
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);

		vbox.getChildren()
				.addAll(label, hbox);
		hbox.getChildren()
				.addAll(comboBox, button);

		comboBox.setValue(BufferingMode.ByChar);


		var waitGroup = new Semaphore(pliki.size(), true);
		var monitor = new TwoWayMonitor();
		var buffer = new Character[1];
		var bufferOwnership = new Semaphore(1, true);

		var out = new WriterThread(
				Files.newBufferedWriter(Path.of(wynik.toURI())),
				Path.of(wynik.toURI())
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

		for (int i = 0; i < pliki.size(); i++) {
			var f = pliki.get(i);
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
				rt.window.setX(20 + stage.getWidth() + 20 + out.window.getWidth() + 20 + (rt.window.getWidth() + 20) * i);
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

		button.setOnMouseClicked(e -> {
			readers.forEach(r -> r.setBufferingMode(comboBox.getValue()));
			readers.forEach(Thread::start);
			out.start();
			button.setOnMouseClicked(Event::consume);
			button.setDisable(true);
		});

		System.out.println(this.getParameters()
				.getRaw());
	}
}

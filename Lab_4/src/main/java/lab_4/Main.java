package lab_4;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.Monitor;
import lab_4.io.ReaderThread;
import lab_4.io.WriterThread;

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

public class Main
		extends Application
{

	public static final List<URL> pliki;
	public static final URL wynik;

	static {
		try {
			pliki = new ArrayList<>();
			Arrays.stream(new File("src/main/resources/in").listFiles())
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

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage)
	throws URISyntaxException, IOException, InterruptedException {
		var l = new Label();
		var scene = new Scene(l, 360, 240);
		stage.setTitle("Zadanie 1");
		stage.setScene(scene);
		stage.show();
		stage.setX(20);
		stage.setY(20);
		l.setText("Okno główne aplikacji\n" +
				"Zamknięcie tego okna spowoduje bezpieczne zamknięcie aplikacji\n" +
				"Kliknij tutaj aby uruchomić program");
		l.setAlignment(Pos.CENTER);
		l.setTextAlignment(TextAlignment.CENTER);

		var waitGroup = new Semaphore(pliki.size());
		var monitor = new Monitor();
		var buffer = new Character[1];

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
						waitGroup
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

			} catch (InterruptedException | URISyntaxException | IOException e) {
				throw new RuntimeException(e);
			}
		}

		stage.setOnCloseRequest(e -> {
			out.interrupt();
			readers.forEach(Thread::interrupt);
			System.exit(0);
		});

		l.setOnMouseClicked(e -> {
			readers.forEach(Thread::start);
			out.start();
		});

		System.out.println(this.getParameters()
				.getRaw());
	}
}

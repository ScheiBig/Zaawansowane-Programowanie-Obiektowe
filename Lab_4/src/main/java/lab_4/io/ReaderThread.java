package lab_4.io;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lab_4.Main;
import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.Monitor;

import java.io.BufferedReader;
import java.io.IOException;

public class ReaderThread
		extends Thread
{

	private final BufferedReader file;
	private final Character[] buffer;
	private final Monitor monitor;
	private final Semaphore sem;
	public final Stage window;
	private final StringProperty windowText;


	public ReaderThread(
			BufferedReader file,
			String name,
			Character[] buffer,
			Monitor monitor,
			Semaphore sem
	)
	throws InterruptedException {
		this.file = file;
		this.buffer = buffer;
		this.monitor = monitor;
		this.sem = sem;

		var ta = new TextArea();
		ta.setEditable(false);
		ta.setFont(Font.font("monospace", 13));

		this.windowText = ta.textProperty();
		this.window = new Stage();
		this.window.setScene(new Scene(ta, 360.0, 640.0));
		this.window.setTitle(name);
		this.window.setOnCloseRequest(Event::consume);

		this.sem.acquire();
		this.setName("ReaderThread :: " + this.getName());
	}

	@Override
	public void run() {
		this.runByChar();
		this.sem.release();
	}

	private void runByChar() {
		try {
			for (int c = file.read(); c != -1; c = file.read()) {
				monitor.lock();
				try {
					while (this.buffer[0] != null) {
						monitor.await();
					}
					this.buffer[0] = (char) c;
					var s = this.buffer[0].toString()
							.equals("\n") ? "↲\n" : this.buffer[0].toString();

					Platform.runLater(() -> {
						this.windowText.setValue(this.windowText.getValue() + s);
					});
					sleep(Main.BOUNCE__MS);
//					System.out.println("-> " + this.buffer[0]);
					this.monitor.signal();
				} finally {
					monitor.unlock();
				}
			}
			Platform.runLater(() -> this.window.setTitle("ZAKOŃCZONO: " + this.window.getTitle()));
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void runByLine() {

	}
}

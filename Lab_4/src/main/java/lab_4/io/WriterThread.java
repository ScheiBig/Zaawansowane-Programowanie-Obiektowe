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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WriterThread
		extends Thread
{
	private final BufferedWriter file;
	private final Character[] buffer;
	private final Monitor mon;
	private final Semaphore sem;
	public final Stage window;
	private final StringProperty windowText;


	public WriterThread(
			BufferedWriter file,
			String name,
			Character[] buffer,
			Monitor mon,
			Semaphore sem
	) {
		this.file = file;
		this.buffer = buffer;
		this.mon = mon;
		this.sem = sem;

		var ta = new TextArea();
		ta.setEditable(false);
		ta.setFont(Font.font("monospace", 16));

		this.windowText = ta.textProperty();
		this.window = new Stage();
		this.window.setScene(new Scene(ta, 1000.0, 640.0));
		this.window.setTitle(name);
		this.window.setOnCloseRequest(Event::consume);

		this.setName("WriterThread :: " + this.getName());
	}

	@Override
	public void run() {
		try {
			outer:
			while (true) {
				mon.lock();
				try {
					while (this.buffer[0] == null) {
						if (!mon.await(Main.ASSUME_THREAD_DEAD__MS, TimeUnit.MILLISECONDS) &&
								sem.tryAcquire(
										sem.permitStorage(),
										Main.ASSUME_THREAD_DEAD__MS,
										TimeUnit.MILLISECONDS
								)) {
							break outer;
						}
					}
//					System.out.println("<- " + this.buffer[0]);
					file.write(this.buffer[0]);
					Platform.runLater(() -> {
						var s = this.buffer[0].toString();
						this.windowText.setValue(this.windowText.getValue() + s);
					});
					this.buffer[0] = null;
					mon.signal();
				} finally {
					mon.unlock();
				}
			}
			file.flush();
			this.window.setTitle("ZAKOŃCZONO: " + this.window.getTitle());
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}

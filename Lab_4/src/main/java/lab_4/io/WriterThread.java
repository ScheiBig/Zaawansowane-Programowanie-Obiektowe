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
import lab_4.concurrent.locks.TwoWayMonitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WriterThread
		extends Thread
{
	public static boolean noGui = false;

	private final BufferedWriter file;
	private final Character[] buffer;
	private final TwoWayMonitor monitor;
	private final Semaphore waitGroup;
	public final Stage window;
	private final StringProperty windowText;


	public WriterThread(
			BufferedWriter file,
			String name,
			Character[] buffer,
			TwoWayMonitor monitor,
			Semaphore waitGroup
	) {
		this.file = file;
		this.buffer = buffer;
		this.monitor = monitor;
		this.waitGroup = waitGroup;

		if (!noGui) {
			var ta = new TextArea();
			ta.setEditable(false);
			ta.setFont(Font.font("monospace", 13));

			this.windowText = ta.textProperty();
			this.window = new Stage();
			this.window.setScene(new Scene(ta, 1000.0, 640.0));
			this.window.setTitle(name);
			this.window.setOnCloseRequest(Event::consume);
		} else {
			this.windowText = null;
			this.window = null;
		}

		this.setName("WriterThread :: " + this.getName());
	}

	@Override
	public void run() {
		try {
			outer:
			while (true) {
				monitor.lock();
				try {
					while (this.buffer[0] == null) {
						if (!monitor.write.await(
								Main.ASSUME_THREAD_DEAD__MS,
								TimeUnit.MILLISECONDS
						) &&
								waitGroup.tryAcquire(
										waitGroup.permitStorage(),
										Main.ASSUME_THREAD_DEAD__MS,
										TimeUnit.MILLISECONDS
								)) {
							break outer;
						}
					}
//					System.out.println("<- " + this.buffer[0]);
					file.write(this.buffer[0]);
					var s = this.buffer[0].toString()
							.equals("\n") ? "↲\n" : this.buffer[0].toString();
					if (!noGui) {
						Platform.runLater((() -> {
							this.windowText.setValue(this.windowText.getValue() + s);
						}));
					}
					this.buffer[0] = null;
					monitor.read.signalAll();
				} finally {
					monitor.unlock();
				}
			}
			file.flush();
			if (!noGui) {
				Platform.runLater(() -> {
					this.window.setTitle("ZAKOŃCZONO: " + this.window.getTitle());
				});
			}
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}

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

import java.io.BufferedReader;
import java.io.IOException;

public class ReaderThread
		extends Thread
{
	public static boolean noGui = false;

	private final BufferedReader file;
	private final Character[] buffer;
	private final TwoWayMonitor monitor;
	private final Semaphore waitGroup;
	private final Semaphore bufferOwnership;
	private BufferingMode bufferingMode;
	public final Stage window;
	private final StringProperty windowText;
	private boolean hasPermit = false;


	public ReaderThread(
			BufferedReader file,
			String name,
			Character[] buffer,
			TwoWayMonitor monitor,
			Semaphore waitGroup
	)
	throws InterruptedException {
		this(file, name, buffer, monitor, waitGroup, null, BufferingMode.ByChar);
	}

	public ReaderThread(
			BufferedReader file,
			String name,
			Character[] buffer,
			TwoWayMonitor monitor,
			Semaphore waitGroup,
			Semaphore bufferOwnership,
			BufferingMode bufferingMode
	)
	throws InterruptedException {
		this.file = file;
		this.buffer = buffer;
		this.monitor = monitor;
		this.waitGroup = waitGroup;

		this.bufferOwnership = bufferOwnership;
		this.bufferingMode = bufferingMode;

		if (!noGui) {
			var ta = new TextArea();
			ta.setEditable(false);
			ta.setFont(Font.font("monospace", 13));

			this.windowText = ta.textProperty();
			this.window = new Stage();
			this.window.setScene(new Scene(ta, 360.0, 640.0));
			this.window.setTitle(name);
			this.window.setOnCloseRequest(Event::consume);
		} else {
			this.windowText = null;
			this.window = null;
		}
		this.waitGroup.acquire();
		this.setName("ReaderThread :: " + this.getName());
	}

	@Override
	public void run() {
		try {
			for (int c = file.read(); c != -1; c = file.read()) {
				if (this.bufferingMode != BufferingMode.ByChar && !this.hasPermit) {
					this.bufferOwnership.acquire();
					this.hasPermit = true;
				}

				monitor.lock();
				try {
					while (this.buffer[0] != null) {
						monitor.read.await();
					}

					this.buffer[0] = (char) c;
					var s = this.buffer[0].toString()
							.equals("\n") ? "↲\n" : this.buffer[0].toString();

					if (!noGui) {
						Platform.runLater(() -> {
							this.windowText.setValue(this.windowText.getValue() + s);
						});
					}
					sleep(Main.BOUNCE__MS);
//					System.out.println("-> " + this.buffer[0]);
					this.monitor.write.signal();
				} finally {
					monitor.unlock();
				}

				if (this.bufferingMode == BufferingMode.ByWord && Character.isWhitespace(c)) {
					this.bufferOwnership.release();
					this.hasPermit = false;
				}

				if (this.bufferingMode == BufferingMode.ByLine && c == '\n') {
					this.bufferOwnership.release();
					this.hasPermit = false;
				}
			}
			if (!noGui) {
				Platform.runLater(() -> {
					this.window.setTitle("ZAKOŃCZONO: " + this.window.getTitle());
				});
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		this.waitGroup.release();
	}

	public BufferingMode getBufferingMode() {
		return bufferingMode;
	}

	public void setBufferingMode(BufferingMode bufferingMode) {
		this.bufferingMode = bufferingMode;
	}
}

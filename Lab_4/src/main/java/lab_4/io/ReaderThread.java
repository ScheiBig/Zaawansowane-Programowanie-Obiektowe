package lab_4.io;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lab_4.Config;
import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.TwoWayMonitor;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A thread, that is responsible for <b>reading from a file</b> into a buffer.
 * <p>
 * Additionally, thread can render {@link javafx JavaFX} GUI, if {@link ReaderThread#noGui} is
 * set to <code>true</code>.
 */
public class ReaderThread
		extends Thread
{
	/**
	 * Flag that indicates, whether class is rendering its own {@link Stage}.
	 */
	public static boolean noGui = false;

	private final BufferedReader file;
	private final Character[] buffer;
	private final TwoWayMonitor monitor;
	private final Semaphore waitGroup;
	private final Semaphore bufferOwnership;
	private BufferingMode bufferingMode;
	/**
	 * A {@link Stage} that displays state of reading.
	 * <p>
	 * It should be true, that:
	 * <pre><code>
	 *     (this.window != null) == this.noGui
	 * </code></pre>
	 */
	public final Stage window;
	private final StringProperty windowText;
	private boolean hasPermit = false;


	/**
	 * Creates new <code>ReaderThread</code>, with implied buffering mode of
	 * {@link BufferingMode#ByChar}.
	 * @param file A {@link BufferedReader} that will provide text to read.
	 * @param name A name of file, used for {@link ReaderThread#window}s title.
	 * @param buffer A one-element array of characters, shared between reader and writer threads.
	 * @param monitor A monitor, used to protect buffer.
	 * @param waitGroup A semaphore, used by readers to signal end-of-file - number of permits in
	 *                    semaphore should be equal to number of launched threads.
	 * @throws InterruptedException if <code>waitGroup</code> is provided with remaining permits,
	 * then probably never.
	 */
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

	/**
	 * Creates new <code>ReaderThread</code>.
	 * @param file A {@link BufferedReader} that will provide text to read.
	 * @param name A name of file, used for {@link ReaderThread#window}s title.
	 * @param buffer A one-element array of characters, shared between reader and writer threads.
	 * @param monitor A monitor, used to protect buffer.
	 * @param waitGroup A semaphore, used by readers to signal end-of-file - number of permits in
	 *                    semaphore should be equal to number of launched threads.
	 * @param bufferOwnership A semaphore, that is used to hold ownership of buffer - if
	 *                           bufferingMode is not {@link BufferingMode#ByChar}, then it must
	 *                           be provided, with only one permit.
	 * @param bufferingMode A mode of reading characters into buffer.
	 * @throws InterruptedException if <code>waitGroup</code> is provided with remaining permits,
	 * then probably never.
	 */
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
					sleep(Config.Bounce_ms);
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

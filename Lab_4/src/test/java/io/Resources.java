package io;

import java.net.URL;

public final class Resources {
	public static final URL test1 = Thread.currentThread()
			.getContextClassLoader()
			.getResource("test1.txt");
	public static final URL test2 = Thread.currentThread()
			.getContextClassLoader()
			.getResource("test2.txt");
	public static final URL test3 = Thread.currentThread()
			.getContextClassLoader()
			.getResource("test3.txt");
}

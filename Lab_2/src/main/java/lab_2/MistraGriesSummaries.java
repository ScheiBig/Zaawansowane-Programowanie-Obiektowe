package lab_2;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MistraGriesSummaries<T extends Comparable<T>> {

	private final Supplier<Stream<T>> valueStreamSupplier;
	private final int bufferSize;

	private Map<T, Integer> buffer;

	private long keyCount;

	public MistraGriesSummaries(
			Supplier<Stream<T>> valueStreamSupplier,
			int bufferSize
	) {
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Size of buffer `k` cannot be less than 1");
		}

		this.valueStreamSupplier = valueStreamSupplier;
		this.bufferSize = bufferSize;
		this.buffer = new HashMap<>();
	}

	public MistraGriesSummaries(
			ResourceSupplier<Stream<T>> valueStreamResourceSupplier,
			int bufferSize
	)
	throws IOException {
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Size of buffer `k` cannot be less than 1");
		}
		// Check if stream is able to be supplied
		valueStreamResourceSupplier.get().close();

		this.valueStreamSupplier = () -> {
			try {
				return valueStreamResourceSupplier.get();
			} catch (IOException e) {
				throw new IllegalStateException("Stream should always be able to produce", e);
			}
		};

		this.bufferSize = bufferSize;
		this.buffer = new HashMap<>();
	}

	@Contract("-> this")
	public MistraGriesSummaries<T> calculated() {
		var stream = this.valueStreamSupplier.get();

		stream.forEach(elem -> {
			this.keyCount++;
			if (this.buffer.containsKey(elem)) {
				this.buffer.put(elem, this.buffer.get(elem) + 1);
			} else if (buffer.size() < bufferSize) {
				this.buffer.put(elem, 1);
			} else {
				this.buffer = this.buffer.entrySet()
						.stream()
						.map(e -> Map.entry(e.getKey(), e.getValue() - 1))
						.filter(e -> e.getValue() > 0)
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			}
		});

		stream.close();
		return this;
	}

	public List<Summary<T>> summaries() {
		var countBuffer = this.buffer.keySet()
				.stream()
				.map(integer -> Map.entry(integer, 0))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		var stream = this.valueStreamSupplier.get();
		stream.forEach(elem -> {
			if (countBuffer.containsKey(elem)) {
				countBuffer.put(elem, countBuffer.get(elem) + 1);
			}
		});

		stream.close();
		var summaries = countBuffer.entrySet()
				.stream()
				.map(e -> new Summary<>(
						e.getKey(),
						(double) e.getValue() / this.keyCount,
						e.getValue()
				))
				.sorted(Comparator.comparingDouble(Summary::occurrenceFrequency))
				.toList();
		return summaries.reversed();
	}

	public record Summary<T>(T key, double occurrenceFrequency, long occurrenceCount) {}
}
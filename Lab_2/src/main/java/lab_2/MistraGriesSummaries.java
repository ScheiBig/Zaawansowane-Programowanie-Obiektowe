package lab_2;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MistraGriesSummaries<T extends Comparable<T>> {

	private final Supplier<Stream<T>> valueStreamSupplier;
	private final int bufferSize;
	private final double targetFrequency;

	private Map<T, Integer> buffer;

	private long keyCount;

	public MistraGriesSummaries(
			Supplier<Stream<T>> valueStreamSupplier,
			int bufferSize,
			double targetFrequency
	) {
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Size of buffer `k` cannot be less than 1");
		}
		if (0.0 > targetFrequency || targetFrequency > 1.0) {
			throw new IllegalArgumentException("Target frequency must be in range [0..1]");
		}

		this.valueStreamSupplier = valueStreamSupplier;
		this.bufferSize = bufferSize;
		this.buffer = new HashMap<>();
		this.targetFrequency = targetFrequency;
	}

	@Contract("-> this")
	public MistraGriesSummaries<T> calculated() {
		this.valueStreamSupplier.get().forEach(elem -> {
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

		return this;
	}

	public List<Summary<T>> summaries() {
		var countBuffer = this.buffer.keySet()
				.stream()
				.map(integer -> Map.entry(integer, 0))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		this.valueStreamSupplier.get().forEach(elem -> {
			if (countBuffer.containsKey(elem)) {
				countBuffer.put(elem, countBuffer.get(elem) + 1);
			}
		});

		return countBuffer.entrySet()
				.stream()
				.map(e -> new Summary<>(
						e.getKey(),
						(double) e.getValue() / this.keyCount,
						e.getValue()
				))
				.toList();
	}

	public record Summary<T>(T key, double occurrenceFrequency, long occurrenceCount) {}
}
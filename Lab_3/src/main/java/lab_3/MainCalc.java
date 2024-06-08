package lab_3;

import lab_3.number.NumberOps;
import lab_3.util.Args;
import lab_3.result.Res;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public class MainCalc {
	private static String currentArg;
	private static double firstOp;
	private static double secondOp;
	private static String operation;

	public static void mainCalc(@Nullable String[] args)
	throws
			InvocationTargetException,
			IllegalAccessException,
			IllegalArgumentException,
			UnsupportedOperationException {

		currentArg = null;
		firstOp = 0;
		secondOp = 0;
		operation = null;

		Iterator<String> argz;
		if (args == null) {
			argz = Args.stdin();
			System.out.println("Enter equation:");
		} else {
			argz = Args.of(args);
		}

		var numOps = NumberOps.class.getDeclaredMethods();
		Method op;

		if (!argz.hasNext()) {
			throw new IllegalArgumentException("You need to provide arguments for calculator!");
		}
		currentArg = argz.next();

		var firstArg = Res.from(() -> Double.parseDouble(currentArg));
		if (firstArg.err() == null) {
			firstOp = firstArg.val();
		} else {
			operation = currentArg;
		}

		if (!argz.hasNext()) {
			throw new IllegalArgumentException(
					"You need to provide at least arguments for calculator!");
		}
		currentArg = argz.next();

		if (operation != null) {
			op = Arrays.stream(numOps)
					.filter(m -> m.getName()
							.equals(operation))
					.findFirst()
					.orElseThrow(() -> new UnsupportedOperationException("Single-argument " +
							"operation " +
							operation +
							" is not implemented"));

			var secondArg = Res.from(() -> Double.parseDouble(currentArg));
			if (secondArg.err() == null) {
				firstOp = secondArg.val();
			} else {
				throw new IllegalArgumentException("Argument " +
						currentArg +
						" is not a number - required for single-argument operation " +
						operation);
			}

			System.out.println(operation + "( " + firstOp + " ) = " + op.invoke(null, firstOp));
			return;
		}

		operation = currentArg;

		op = Arrays.stream(numOps)
				.filter(m -> m.getName()
						.equals(operation))
				.findFirst()
				.orElseThrow(() -> new UnsupportedOperationException("Double-argument operation " +
						operation +
						" is not implemented"));


		if (!argz.hasNext()) {
			throw new IllegalArgumentException(
					"You need to provide at three arguments for calculator with operation " +
							operation +
							"!");
		}

		currentArg = argz.next();

		var secondArg = Res.from(() -> Double.parseDouble(currentArg));
		if (secondArg.err() == null) {
			secondOp = secondArg.val();
		} else {
			throw new IllegalArgumentException("Argument " +
					currentArg +
					" is not a number - required for double-argument operation " +
					operation);
		}

		System.out.println(operation +
				"( " +
				firstOp +
				", " +
				secondOp +
				" ) = " +
				op.invoke(null, firstOp, secondOp));
	}
}

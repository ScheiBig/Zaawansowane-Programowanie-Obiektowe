package lab_3;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MainCalcTests {

	private static PrintStream stdout;


	@BeforeAll
	static void set_up() {
		stdout = System.out;
	}

	@AfterAll
	static void tear_down() {
		System.setOut(stdout);
	}

	private static ByteArrayOutputStream getNewOut() {
		var baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
		return baos;
	}

	@Test
	void Test_no_args() {
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> MainCalc.mainCalc(new String[]{})
		);
	}


	@Test
	void Test_only_one_arg() {
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> MainCalc.mainCalc(new String[]{"1"})
		);
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> MainCalc.mainCalc(new String[]{"sqrt"})
		);
	}

	@Test
	void Test_unknown_single_arg_operation() {
		Assertions.assertThrows(
				UnsupportedOperationException.class,
				() -> MainCalc.mainCalc(new String[]{"sin", "3.14"})
		);
	}

	@Test
	void Test_bad_number_for_single_arg_operation() {
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> MainCalc.mainCalc(new String[]{"sqrt", "PI"})
		);
	}

	@Test
	void Test_unknown_double_arg_operation() {
		Assertions.assertThrows(
				UnsupportedOperationException.class,
				() -> MainCalc.mainCalc(new String[]{"2", "power", "3"})
		);
	}

	@Test
	void Test_bad_second_number_for_double_arg_operation() {
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> MainCalc.mainCalc(new String[]{"2", "times", "PI"})
		);
	}

	@Test
	void Test_proper_calculations()
	throws InvocationTargetException, IllegalAccessException {
		var os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"sqrt", "4"}));
		Assertions.assertEquals(
				"sqrt( 4.0 ) = 2.0",
				os.toString()
						.strip()
		);

		os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"sqrt", "-1"}));
		Assertions.assertEquals(
				"sqrt( -1.0 ) = NaN",
				os.toString()
						.strip()
		);

		os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"9", "plus", "10"}));
		Assertions.assertEquals(
				"plus( 9.0, 10.0 ) = 19.0",
				os.toString()
						.strip()
		);

		os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"6", "minus", "9"}));
		Assertions.assertEquals(
				"minus( 6.0, 9.0 ) = -3.0",
				os.toString()
						.strip()
		);

		os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"21", "times", "37"}));
		Assertions.assertEquals(
				"times( 21.0, 37.0 ) = 777.0",
				os.toString()
						.strip()
		);

		os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"-1", "divide", "-2"}));
		Assertions.assertEquals(
				"divide( -1.0, -2.0 ) = 0.5",
				os.toString()
						.strip()
		);

		os = getNewOut();
		Assertions.assertDoesNotThrow(() -> MainCalc.mainCalc(new String[]{"42", "divide", "0"}));
		Assertions.assertEquals(
				"divide( 42.0, 0.0 ) = Infinity",
				os.toString()
						.strip()
		);
	}
}

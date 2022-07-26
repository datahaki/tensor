// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Round;

class CsvHelperTest {
  @Test
  void testFraction() {
    Scalar scalar = CsvHelper.FUNCTION.apply(RationalScalar.of(1, 2));
    assertEquals(scalar.toString(), "0.5");
  }

  @Test
  void testInteger() {
    Scalar scalar = CsvHelper.FUNCTION.apply(RealScalar.of(-2345891274545L));
    assertEquals(scalar.toString(), "-2345891274545");
  }

  @Test
  void testString1() {
    Scalar scalar = StringScalar.of("abc!");
    assertEquals(CsvHelper.FUNCTION.apply(scalar).toString(), "\"abc!\"");
  }

  @Test
  void testString2() {
    String string = "\"abc!\"";
    Scalar scalar = StringScalar.of(string);
    assertEquals(CsvHelper.FUNCTION.apply(scalar).toString(), string);
  }

  @Test
  void testDecimal() {
    Scalar scalar = (Scalar) DoubleScalar.of(0.25).map(Round._6);
    assertInstanceOf(DecimalScalar.class, scalar);
    scalar = CsvHelper.FUNCTION.apply(scalar);
    assertInstanceOf(DoubleScalar.class, scalar);
  }

  @Test
  void testQuotes() {
    Scalar inQuotes = StringScalar.of("\"abc\"");
    assertEquals(CsvHelper.wrap(StringScalar.of("abc")), inQuotes);
    assertEquals(CsvHelper.wrap(inQuotes), inQuotes);
  }

  @Test
  void testSingleInQuotes() {
    Scalar inQuotes = StringScalar.of("\"a\"");
    assertEquals(CsvHelper.wrap(StringScalar.of("a")), inQuotes);
    assertEquals(CsvHelper.wrap(inQuotes), inQuotes);
  }

  @Test
  void testEmpty() {
    Scalar inQuotes = StringScalar.of("\"\"");
    assertEquals(CsvHelper.wrap(StringScalar.of("")), inQuotes);
    assertEquals(CsvHelper.wrap(inQuotes), inQuotes);
  }

  @Test
  void testComplexFail() {
    assertThrows(Throw.class, () -> CsvHelper.FUNCTION.apply(ComplexScalar.of(3, 4)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> CsvHelper.FUNCTION.apply(Quantity.of(3, "s")));
  }

  @Test
  void testFailSingleQuote() {
    CsvHelper.requireQuotesFree("");
    assertThrows(StringIndexOutOfBoundsException.class, () -> CsvHelper.wrap(StringScalar.of("\"")));
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> CsvHelper.wrap(StringScalar.of("\"abc\"\"")));
    assertThrows(IllegalArgumentException.class, () -> CsvHelper.wrap(StringScalar.of("abc\"")));
    assertThrows(IllegalArgumentException.class, () -> CsvHelper.wrap(StringScalar.of("\"abc")));
  }
}

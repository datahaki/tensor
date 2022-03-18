// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.usr.AssertFail;

public class CsvHelperTest {
  @Test
  public void testFraction() {
    Scalar scalar = CsvHelper.FUNCTION.apply(RationalScalar.of(1, 2));
    assertEquals(scalar.toString(), "0.5");
  }

  @Test
  public void testInteger() {
    Scalar scalar = CsvHelper.FUNCTION.apply(RealScalar.of(-2345891274545L));
    assertEquals(scalar.toString(), "-2345891274545");
  }

  @Test
  public void testString1() {
    Scalar scalar = StringScalar.of("abc!");
    assertEquals(CsvHelper.FUNCTION.apply(scalar).toString(), "\"abc!\"");
  }

  @Test
  public void testString2() {
    String string = "\"abc!\"";
    Scalar scalar = StringScalar.of(string);
    assertEquals(CsvHelper.FUNCTION.apply(scalar).toString(), string);
  }

  @Test
  public void testDecimal() {
    Scalar scalar = (Scalar) DoubleScalar.of(0.25).map(Round._6);
    assertTrue(scalar instanceof DecimalScalar);
    scalar = CsvHelper.FUNCTION.apply(scalar);
    assertTrue(scalar instanceof DoubleScalar);
  }

  @Test
  public void testQuotes() {
    Scalar inQuotes = StringScalar.of("\"abc\"");
    assertEquals(CsvHelper.wrap(StringScalar.of("abc")), inQuotes);
    assertEquals(CsvHelper.wrap(inQuotes), inQuotes);
  }

  @Test
  public void testSingleInQuotes() {
    Scalar inQuotes = StringScalar.of("\"a\"");
    assertEquals(CsvHelper.wrap(StringScalar.of("a")), inQuotes);
    assertEquals(CsvHelper.wrap(inQuotes), inQuotes);
  }

  @Test
  public void testEmpty() {
    Scalar inQuotes = StringScalar.of("\"\"");
    assertEquals(CsvHelper.wrap(StringScalar.of("")), inQuotes);
    assertEquals(CsvHelper.wrap(inQuotes), inQuotes);
  }

  @Test
  public void testComplexFail() {
    AssertFail.of(() -> CsvHelper.FUNCTION.apply(ComplexScalar.of(3, 4)));
  }

  @Test
  public void testQuantityFail() {
    AssertFail.of(() -> CsvHelper.FUNCTION.apply(Quantity.of(3, "s")));
  }

  @Test
  public void testFailSingleQuote() {
    CsvHelper.requireQuotesFree("");
    AssertFail.of(() -> CsvHelper.wrap(StringScalar.of("\"")));
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> CsvHelper.wrap(StringScalar.of("\"abc\"\"")));
    AssertFail.of(() -> CsvHelper.wrap(StringScalar.of("abc\"")));
    AssertFail.of(() -> CsvHelper.wrap(StringScalar.of("\"abc")));
  }
}

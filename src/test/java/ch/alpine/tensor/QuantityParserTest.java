// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class QuantityParserTest {
  @Test
  public void testSome() {
    Scalar scalar = QuantityParser.of("3.25[]");
    assertTrue(scalar instanceof DoubleScalar);
    assertEquals(scalar, RealScalar.of(3.25));
  }

  @Test
  public void testBug() {
    AssertFail.of(() -> QuantityParser.of("1[m2]"));
  }

  @Test
  public void testNestedFail() {
    AssertFail.of(() -> QuantityParser.of("1[s][m]"));
  }
}

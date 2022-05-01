// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class QuantityParserTest {
  @Test
  public void testSome() {
    Scalar scalar = QuantityParser.of("3.25[]");
    assertInstanceOf(DoubleScalar.class, scalar);
    assertEquals(scalar, RealScalar.of(3.25));
  }

  @Test
  public void testBug() {
    assertThrows(IllegalArgumentException.class, () -> QuantityParser.of("1[m2]"));
  }

  @Test
  public void testNestedFail() {
    assertThrows(IllegalArgumentException.class, () -> QuantityParser.of("1[s][m]"));
  }
}

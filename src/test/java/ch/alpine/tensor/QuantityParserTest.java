// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class QuantityParserTest {
  @Test
  void testSome() {
    Scalar scalar = QuantityParser.of("3.25[]");
    assertInstanceOf(DoubleScalar.class, scalar);
    assertEquals(scalar, RealScalar.of(3.25));
  }

  @Test
  void testBug() {
    assertThrows(IllegalArgumentException.class, () -> QuantityParser.of("1[m2]"));
  }

  @Test
  void testNestedFail() {
    assertThrows(IllegalArgumentException.class, () -> QuantityParser.of("1[s][m]"));
  }

  @Test
  void testMutationTest() {
    assertThrows(Exception.class, () -> ScalarParser.of("[m]"));
    assertThrows(Exception.class, () -> QuantityParser.of("[m]"));
    assertThrows(Exception.class, () -> QuantityParser.of("12[m"));
    assertThrows(Exception.class, () -> QuantityParser.of("12][m"));
  }
}

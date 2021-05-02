// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuantityParserTest extends TestCase {
  public void testSome() {
    Scalar scalar = QuantityParser.of("3.25[]");
    assertTrue(scalar instanceof DoubleScalar);
    assertEquals(scalar, RealScalar.of(3.25));
  }

  public void testBug() {
    AssertFail.of(() -> QuantityParser.of("1[m2]"));
  }

  public void testNestedFail() {
    AssertFail.of(() -> QuantityParser.of("1[s][m]"));
  }
}

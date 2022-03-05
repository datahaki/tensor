// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TanTest extends TestCase {
  public void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Tan.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.tan(2));
    assertEquals(c, Tan.of(i));
    assertEquals(c, s);
  }

  public void testComplex() {
    Scalar c = Tan.of(ComplexScalar.of(2, 3.));
    Scalar s = Scalars.fromString("-0.0037640256415042484 + 1.0032386273536098*I"); // Mathematica
    Chop._15.requireClose(s, c);
  }

  public void testTypeFail() {
    Scalar scalar = StringScalar.of("some");
    AssertFail.of(() -> Tan.of(scalar));
  }
}

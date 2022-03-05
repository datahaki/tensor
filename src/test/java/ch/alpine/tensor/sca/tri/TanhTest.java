// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TanhTest extends TestCase {
  public void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Tanh.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.tanh(2));
    assertEquals(c, Tanh.of(i));
    assertEquals(c, s);
  }

  public void testComplex() {
    Scalar c = Tanh.of(ComplexScalar.of(2, 3.));
    Scalar s = Scalars.fromString("0.965385879022133 - 0.009884375038322494*I"); // Mathematica
    Chop._13.requireClose(c, s);
  }

  public void testFail() {
    Scalar scalar = GaussScalar.of(3, 11);
    AssertFail.of(() -> Tanh.of(scalar));
  }
}

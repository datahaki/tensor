// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;
import junit.framework.TestCase;

public class RotateTest extends TestCase {
  public void testSimple() {
    Rotate rotate = new Rotate(RealScalar.of(3), RealScalar.of(4), RealScalar.of(7), RealScalar.of(11));
    Scalar scalar = Scalars.fromString("(3+4*I)*(7+11*I)");
    assertEquals(rotate.re(), Real.FUNCTION.apply(scalar));
    assertEquals(rotate.im(), Imag.FUNCTION.apply(scalar));
    ExactScalarQ.require(rotate.re());
    ExactScalarQ.require(rotate.im());
  }
}

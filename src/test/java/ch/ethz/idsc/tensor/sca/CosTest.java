// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class CosTest extends TestCase {
  public void testReal() {
    Scalar c = Cos.FUNCTION.apply(RealScalar.of(2));
    Scalar s = DoubleScalar.of(Math.cos(2));
    assertEquals(c, s);
  }

  public void testComplex() {
    Scalar c = Cos.FUNCTION.apply(ComplexScalar.of(2, 3.));
    // -4.1896256909688072301 - 9.1092278937553365980 I
    Scalar s = Scalars.fromString("-4.189625690968807-9.109227893755337*I");
    assertEquals(c, s);
  }
}

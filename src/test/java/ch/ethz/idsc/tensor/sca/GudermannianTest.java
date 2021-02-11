// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class GudermannianTest extends TestCase {
  public void testSimple() {
    Scalar scalar = Gudermannian.FUNCTION.apply(RationalScalar.HALF);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.48038107913372944860)); // mathematica
  }

  public void testListable() {
    Tensor tensor = Gudermannian.of(Range.of(-3, 4));
    Tolerance.CHOP.requireClose(tensor, Reverse.of(tensor).negate());
  }
}

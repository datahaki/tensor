// code by jph
package ch.alpine.tensor.sca;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.mat.Tolerance;

class GudermannianTest {
  @Test
  void testSimple() {
    Scalar scalar = Gudermannian.FUNCTION.apply(RationalScalar.HALF);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.48038107913372944860)); // mathematica
  }

  @Test
  void testListable() {
    Tensor tensor = Range.of(-3, 4).map(Gudermannian.FUNCTION);
    Tolerance.CHOP.requireClose(tensor, Reverse.of(tensor).negate());
  }
}

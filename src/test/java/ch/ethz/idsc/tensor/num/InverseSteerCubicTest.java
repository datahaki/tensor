// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;
import junit.framework.TestCase;

public class InverseSteerCubicTest extends TestCase {
  public void testSteer() {
    Scalar c = RealScalar.of(+0.8284521034333863);
    Scalar a = RealScalar.of(-0.33633373640449604);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, c, RealScalar.ZERO, a);
    ScalarUnaryOperator cubic = Series.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar d = cubic.apply((Scalar) t);
      Tensor roots = Roots.of(Tensors.of(d.negate(), c, RealScalar.ZERO, a));
      assertEquals(ExactTensorQ.require(Imag.of(roots)), Array.zeros(3));
      Chop._13.requireClose(roots.Get(1), t);
    }
  }

  public void testCubicOp() {
    Scalar b = RealScalar.of(+0.8284521034333863);
    Scalar d = RealScalar.of(-0.33633373640449604);
    InverseSteerCubic inverseSteerCubic = new InverseSteerCubic(b, d);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, d);
    ScalarUnaryOperator cubic = Series.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar apply = cubic.apply((Scalar) t);
      Scalar root = inverseSteerCubic.apply(apply);
      Chop._13.requireClose(root, t);
    }
    assertEquals(inverseSteerCubic.apply(RealScalar.ZERO), RealScalar.ZERO);
  }
}

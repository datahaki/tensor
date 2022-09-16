// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.ply.Roots;

class InverseSteerCubicTest {
  @Test
  void testSteer() {
    Scalar c = RealScalar.of(+0.8284521034333863);
    Scalar a = RealScalar.of(-0.33633373640449604);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, c, RealScalar.ZERO, a);
    ScalarUnaryOperator cubic = Polynomial.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar d = cubic.apply((Scalar) t);
      Tensor roots = Roots.of(Tensors.of(d.negate(), c, RealScalar.ZERO, a));
      assertEquals(ExactTensorQ.require(Imag.of(roots)), Array.zeros(3));
      Chop._13.requireClose(roots.Get(1), t);
    }
  }

  @Test
  void testCubicOp() {
    Scalar b = RealScalar.of(+0.8284521034333863);
    Scalar d = RealScalar.of(-0.33633373640449604);
    InverseSteerCubic inverseSteerCubic = new InverseSteerCubic(b, d);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, d);
    ScalarUnaryOperator cubic = Polynomial.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar apply = cubic.apply((Scalar) t);
      Scalar root = inverseSteerCubic.apply(apply);
      Chop._13.requireClose(root, t);
    }
    assertEquals(inverseSteerCubic.apply(RealScalar.ZERO), RealScalar.ZERO);
  }
}

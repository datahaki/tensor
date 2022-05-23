// code by jph
package ch.alpine.tensor.nrm;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class CorrelationDistanceTest {
  @Test
  void test() {
    Scalar scalar = CorrelationDistance.of(Tensors.vector(1, 2, 3), Tensors.vector(3, 5, 10));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.029274656605849048));
  }
}

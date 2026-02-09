// code by jph
package ch.alpine.tensor.sca.erf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class ErfiTest {
  @Test
  void testSimple() {
    Scalar result = Erfi.FUNCTION.apply(ComplexScalar.of(1.2, 3.4));
    Scalar expect = ComplexScalar.of(4.9665206621382625E-6, 1.0000035775250082);
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  void testOf() {
    Tensor vector = Tensors.vector(1, 2, 3, 4);
    vector.maps(Erfi.FUNCTION);
  }
}

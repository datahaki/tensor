// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class CosineDistanceTest {
  @Test
  void testZero() {
    assertEquals(CosineDistance.of(Tensors.vector(0, 0, 0), Tensors.vector(1, 2, 3)), RealScalar.ZERO);
    assertEquals(CosineDistance.of(Tensors.vector(0, 0, 0.0), Tensors.vector(1, 2, 3)), RealScalar.ZERO);
  }

  @Test
  void testSome() {
    Tolerance.CHOP.requireClose( //
        CosineDistance.of(Tensors.vector(1, 1, 1), Tensors.vector(1, 2, 3)), //
        RealScalar.of(0.07417990022744858));
  }
}

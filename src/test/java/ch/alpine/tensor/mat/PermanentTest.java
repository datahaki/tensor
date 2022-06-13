// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.sca.gam.Factorial;
import ch.alpine.tensor.sca.pow.Power;

class PermanentTest {
  @Test
  void testSimple() {
    Scalar scalar = Permanent.of(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}"));
    assertEquals(scalar, RealScalar.of(450)); // confirmed with mathematica
  }

  @Test
  void testHilbert() {
    Scalar scalar = Permanent.of(HilbertMatrix.of(5));
    assertEquals(scalar, RationalScalar.of(32104903, 470400000)); // confirmed with mathematica
  }

  @RepeatedTest(4)
  void testMin(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = ConstantArray.of(RationalScalar.of(1, n), n, n);
    Scalar scalar = Permanent.of(matrix);
    assertEquals(scalar, Factorial.of(n).divide(Power.of(n, n)));
  }

  @Test
  void testFailAd() {
    assertThrows(IllegalArgumentException.class, () -> Permanent.of(Tensors.empty()));
    assertThrows(IllegalArgumentException.class, () -> Permanent.of(Tensors.vector(1, 2, 3)));
    assertThrows(ClassCastException.class, () -> Permanent.of(Array.zeros(3, 3, 3)));
    assertThrows(IllegalArgumentException.class, () -> Permanent.of(HilbertMatrix.of(2, 3)));
    assertThrows(IllegalArgumentException.class, () -> Permanent.of(HilbertMatrix.of(3, 2)));
  }
}

// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class FoldTest {
  @Test
  void testSimple() {
    Tensor fold = Fold.of(Tensor::dot, LeviCivitaTensor.of(4), IdentityMatrix.of(4));
    assertEquals(fold, RealScalar.ONE);
  }

  @Test
  void testZero() {
    Tensor x = HilbertMatrix.of(3);
    Tensor fold = Fold.of(Tensor::dot, x, Tensors.empty());
    x.set(Scalar::zero, Tensor.ALL, Tensor.ALL);
    assertEquals(x, Array.zeros(3, 3));
    assertEquals(fold, HilbertMatrix.of(3));
  }

  @Test
  void testSingletonStream() {
    assertEquals(Stream.of(Pi.VALUE).reduce(Scalar::add).orElseThrow(), Pi.VALUE);
  }

  @Test
  void testDet() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      assertEquals(Fold.of((t, v) -> v.dot(t), LeviCivitaTensor.of(n), matrix), Det.of(matrix));
    }
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Fold.of(null, Pi.HALF, Tensors.empty()));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Fold.of(Tensor::dot, Pi.HALF, Pi.HALF));
  }
}

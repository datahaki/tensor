// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FoldTest extends TestCase {
  public void testSimple() {
    Tensor fold = Fold.of(Tensor::dot, LeviCivitaTensor.of(4), IdentityMatrix.of(4));
    assertEquals(fold, RealScalar.ONE);
  }

  public void testZero() {
    Tensor x = HilbertMatrix.of(3);
    Tensor fold = Fold.of(Tensor::dot, x, Tensors.empty());
    x.set(Scalar::zero, Tensor.ALL, Tensor.ALL);
    assertEquals(x, Array.zeros(3, 3));
    assertEquals(fold, HilbertMatrix.of(3));
  }

  public void testSingletonStream() {
    assertEquals(Stream.of(Pi.VALUE).reduce(Scalar::add).get(), Pi.VALUE);
  }

  public void testDet() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      assertEquals(Fold.of((t, v) -> v.dot(t), LeviCivitaTensor.of(n), matrix), Det.of(matrix));
    }
  }

  public void testNullFail() {
    AssertFail.of(() -> Fold.of(null, Pi.HALF, Tensors.empty()));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Fold.of(Tensor::dot, Pi.HALF, Pi.HALF));
  }
}

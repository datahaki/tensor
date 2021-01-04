// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class Norm2BoundTest extends TestCase {
  private static void _check(Tensor x) {
    Scalar n2 = Norm._2.ofMatrix(x);
    Scalar ni = Norm2Bound.ofMatrix(x);
    assertTrue(Scalars.lessEquals(n2, ni));
  }

  public void testQuantity() {
    for (int count = 0; count < 5; ++count)
      for (int n = 2; n < 6; ++n) {
        _check(HilbertMatrix.of(n));
        _check(RandomVariate.of(NormalDistribution.standard(), n, n).map(s -> Quantity.of(s, "m")));
        _check(RandomVariate.of(UniformDistribution.of(-0.05, 0.05), n, n).map(s -> Quantity.of(s, "m")));
        _check(RandomVariate.of(UniformDistribution.of(-5, 5), n, n).map(s -> Quantity.of(s, "m")));
      }
  }
}

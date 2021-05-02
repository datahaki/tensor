// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

/** Reference: NR 2007 eq. (2.7.2) */
public class ShermanMorrisonTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    int fails = 0;
    for (int n = 3; n < 8; ++n)
      try {
        Tensor matrix = RandomVariate.of(distribution, n, n);
        Tensor u = RandomVariate.of(distribution, n);
        Tensor v = RandomVariate.of(distribution, n);
        Tensor compar = Inverse.of(matrix.add(TensorProduct.of(u, v)));
        Tensor invers = Inverse.of(matrix);
        Tensor z = invers.dot(u);
        Scalar lambda = (Scalar) v.dot(z);
        Tensor altern = invers.subtract(TensorProduct.of(z, v.dot(invers)).divide(lambda.add(RealScalar.ONE)));
        Tolerance.CHOP.requireClose(compar, altern);
      } catch (Exception exception) {
        ++fails;
      }
    assertTrue(fails < 2);
  }
}

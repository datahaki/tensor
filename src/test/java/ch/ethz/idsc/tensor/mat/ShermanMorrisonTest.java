// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

/** Reference: NR 2007 eq. (2.7.2) */
public class ShermanMorrisonTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    int n = 7;
    Tensor matrix = RandomVariate.of(distribution, n, n);
    Tensor u = RandomVariate.of(distribution, n);
    Tensor v = RandomVariate.of(distribution, n);
    Tensor compar = Inverse.of(matrix.add(TensorProduct.of(u, v)));
    // ---
    Tensor invers = Inverse.of(matrix);
    Tensor z = invers.dot(u);
    Scalar lambda = (Scalar) v.dot(z);
    Tensor altern = invers.subtract(TensorProduct.of(z, v.dot(invers)).divide(lambda.add(RealScalar.ONE)));
    Tolerance.CHOP.requireClose(compar, altern);
  }
}

// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DenmanBeaversTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 2; n < 6; ++n)
      for (int count = 0; count < 10; ++count) {
        Tensor _a = RandomVariate.of(distribution, n, n);
        Tensor a = _a.dot(_a);
        MatrixSqrt denmanBeavers = new DenmanBeavers(a);
        Tensor as = denmanBeavers.sqrt().dot(denmanBeavers.sqrt());
        Chop._08.requireClose(a, as);
        Tensor e = denmanBeavers.sqrt().dot(denmanBeavers.sqrt_inverse());
        Chop._08.requireClose(e, IdentityMatrix.of(n));
      }
  }
  // public void testQuantity() {
  // Tensor x = Tensors.fromString("{{2[m], 2[m]}, {0[m], 3[m]}}");
  // Tensor a = x.dot(x);
  // System.out.println(a);
  // new DenmanBeavers(a);
  // }
}

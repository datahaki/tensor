// code by jph
package ch.alpine.tensor.mat;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

/** Reference: NR 2007 eq. (2.7.2) */
public class ShermanMorrisonTest {
  @Test
  public void testSimple() {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.standard();
    for (int n = 3; n < 8; ++n) {
      Tensor matrix = RandomVariate.of(distribution, random, n, n);
      Tensor u = RandomVariate.of(distribution, random, n);
      Tensor v = RandomVariate.of(distribution, random, n);
      Tensor compar = Inverse.of(matrix.add(TensorProduct.of(u, v)));
      Tensor invers = Inverse.of(matrix);
      Tensor z = invers.dot(u);
      Scalar lambda = (Scalar) v.dot(z);
      Tensor altern = invers.subtract(TensorProduct.of(z, v.dot(invers)).divide(lambda.add(RealScalar.ONE)));
      Tolerance.CHOP.requireClose(compar, altern);
    }
  }
}

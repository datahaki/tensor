// code by jph
package ch.ethz.idsc.tensor.mat.sv;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixDotTranspose;
import ch.ethz.idsc.tensor.alg.OrderedQ;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.mat.ev.Eigensystem;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SingularValueListTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor x = RandomVariate.of(distribution, 3, 4);
    Tensor matrix = MatrixDotTranspose.of(x, x);
    Tensor values1 = Eigensystem.ofSymmetric(matrix).values();
    Tensor values2 = SingularValueList.of(matrix);
    Tolerance.CHOP.requireClose(values1, values2);
    OrderedQ.require(Reverse.of(values2));
  }

  public void testMixedUnitFail() {
    Distribution distribution = DiscreteUniformDistribution.of(1, 4);
    Tensor matrix = RandomVariate.of(distribution, 7, 2);
    matrix.set(s -> Quantity.of((Scalar) s, "s"), Tensor.ALL, 1);
    AssertFail.of(() -> SingularValueDecomposition.of(matrix));
  }
}

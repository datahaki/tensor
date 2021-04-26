// code by jph
package ch.ethz.idsc.tensor.mat.ev;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixDotTranspose;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.mat.sv.SingularValueList;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class EigensystemImplTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor x = RandomVariate.of(distribution, 4, 3);
    Tensor matrix = MatrixDotTranspose.of(x, x);
    Tensor values1 = Eigensystem.ofSymmetric(matrix).values();
    Tensor values2 = SingularValueList.of(matrix);
    Tolerance.CHOP.requireClose(values1, values2);
  }
}

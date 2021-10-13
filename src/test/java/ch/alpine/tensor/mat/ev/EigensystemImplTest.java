// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
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

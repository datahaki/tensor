// code by jph
package ch.alpine.tensor.mat.ev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class EigensystemImplTest {
  @Test
  @Timeout(1)
  void testSimple() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor x = RandomVariate.of(distribution, 4, 3);
    Tensor matrix = MatrixDotTranspose.of(x, x);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor values1 = eigensystem.values();
    Tensor values2 = SingularValueList.of(matrix);
    Tolerance.CHOP.requireClose(values1, values2);
    TestHelper.checkEquation(matrix, eigensystem);
  }
}

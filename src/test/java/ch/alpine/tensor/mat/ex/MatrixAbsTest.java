// code by jph
package ch.alpine.tensor.mat.ex;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

public class MatrixAbsTest {
  @Test
  public void testSimple() {
    Tensor matrix = Symmetrize.of(RandomVariate.of(UniformDistribution.of(-1, 1), 5, 5));
    MatrixAbs.ofSymmetric(matrix);
    MatrixAbs.ofHermitian(matrix);
  }
}

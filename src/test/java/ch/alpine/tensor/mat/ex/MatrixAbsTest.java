// code by jph
package ch.alpine.tensor.mat.ex;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class MatrixAbsTest {
  @Test
  void testSimple() {
    Tensor matrix = Symmetrize.of(RandomVariate.of(UniformDistribution.of(-1, 1), 5, 5));
    Tensor symmetric = MatrixAbs.ofSymmetric(matrix);
    SquareMatrixQ.INSTANCE.requireMember(symmetric);
    Tensor hermitian = MatrixAbs.ofHermitian(matrix);
    SquareMatrixQ.INSTANCE.requireMember(hermitian);
  }
}

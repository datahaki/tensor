// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.Sqrt;

class NilpotentMatrixQTest {
  private static void _check(Tensor matrix) {
    assertTrue(NilpotentMatrixQ.of(matrix));
    assertTrue(NilpotentMatrixQ.of(matrix.map(N.DOUBLE)));
  }

  @Test
  void test234() {
    // Reference: https://en.wikipedia.org/wiki/Nilpotent_matrix
    _check(Tensors.fromString("{{2,-1},{4,-2}}"));
    _check(Tensors.fromString("{{2,2,-2},{5,1,-3},{1,5,-3}}"));
    _check(Tensors.fromString("{{2,2,2,-3},{6,1,1,-4},{1,6,1,-4},{1,1,6,-4}}"));
  }

  @Test
  void test234Quantity() {
    ScalarUnaryOperator suo = s -> Quantity.of(s, "m");
    // Reference: https://en.wikipedia.org/wiki/Nilpotent_matrix
    _check(Tensors.fromString("{{2,-1},{4,-2}}").map(suo));
    _check(Tensors.fromString("{{2,2,-2},{5,1,-3},{1,5,-3}}").map(suo));
    _check(Tensors.fromString("{{2,2,2,-3},{6,1,1,-4},{1,6,1,-4},{1,1,6,-4}}").map(suo));
  }

  @Test
  void testNope() {
    assertFalse(NilpotentMatrixQ.of(IdentityMatrix.of(3)));
    assertFalse(NilpotentMatrixQ.of(DiagonalMatrix.of(3, 0)));
    assertFalse(NilpotentMatrixQ.of(DiagonalMatrix.of(3, -3)));
  }

  @RepeatedTest(3)
  void testScaling(RepetitionInfo repetitionInfo) {
    int i = repetitionInfo.getCurrentRepetition();
    int n = i * 5;
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 100), n, n);
    Scalar n2 = Matrix2Norm.bound(matrix);
    Scalar factor = Sqrt.FUNCTION.apply(RealScalar.of(n)).divide(n2);
    Tensor scaled = matrix.multiply(factor);
    Scalar s1 = Matrix2Norm.bound(scaled);
    Scalar s2 = Matrix2Norm.bound(scaled.dot(scaled));
    Scalar s3 = Matrix2Norm.bound(scaled.dot(scaled).dot(scaled));
    Tensor vec = Tensors.of(n2, s1, s2, s3).map(Round._2);
    // System.out.println(vec);
    Tensor tensor = MatrixPower.of(matrix, n);
    tensor.map(Scalar::zero);
    vec.map(Scalar::zero);
  }
}

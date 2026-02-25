// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import test.bulk.TestDistributions;

class MatrixDotTransposeTest {
  private static Stream<Distribution> distributions() {
    return TestDistributions.distributions();
  }

  @ParameterizedTest
  @MethodSource("distributions")
  void testSimple(Distribution distribution) {
    Tensor matrix = RandomVariate.of(distribution, 3, 5);
    Tensor tensor = Dot.of(matrix, Transpose.of(matrix));
    Tensor result = MatrixDotTranspose.of(matrix, matrix);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  @ParameterizedTest
  @MethodSource("distributions")
  void testTwo(Distribution distribution) {
    Tensor a = RandomVariate.of(distribution, 3, 5);
    Tensor b = RandomVariate.of(distribution, 3, 5);
    Tensor tensor = Dot.of(a, Transpose.of(b));
    Tensor result = MatrixDotTranspose.of(a, b);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  @ParameterizedTest
  @MethodSource("distributions")
  void testRank3(Distribution distribution) {
    Tensor a = RandomVariate.of(distribution, 4, 3);
    Tensor b = LeviCivitaTensor.of(3);
    Tensor tensor = Dot.of(a, Transpose.of(b));
    Tensor result = MatrixDotTranspose.of(a, b);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  @Test
  void testVectorFail() {
    assertThrows(Throw.class, () -> MatrixDotTranspose.of(Tensors.vector(2, 1), Tensors.vector(3, 7)));
    assertThrows(Throw.class, () -> MatrixDotTranspose.of(Tensors.vector(2, 1), HilbertMatrix.of(2, 3)));
    assertThrows(IllegalArgumentException.class, () -> MatrixDotTranspose.of(HilbertMatrix.of(2), Tensors.vector(3, 7)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> MatrixDotTranspose.of(RealScalar.ONE, RealScalar.ONE));
    assertThrows(Throw.class, () -> MatrixDotTranspose.of(RealScalar.ONE, HilbertMatrix.of(2, 3)));
    assertThrows(Throw.class, () -> MatrixDotTranspose.of(HilbertMatrix.of(2), RealScalar.ONE));
  }

  @Test
  void testThreeFail() {
    Tensor a = RandomVariate.of(NormalDistribution.standard(), 3, 5, 4);
    Tensor b = RandomVariate.of(NormalDistribution.standard(), 2, 4);
    // Tolerance.CHOP.requireClose(a.dot(Transpose.of(b)), MatrixDotTranspose.of(a, b));
    // Tolerance.CHOP.requireClose(Dot.of(a, Transpose.of(b)), MatrixDotTranspose.of(a, b));
    assertThrows(ClassCastException.class, () -> MatrixDotTranspose.of(a, b));
  }
}

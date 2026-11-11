// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class TransposeMatrixDotTest {
  @Test
  void test() {
    Tensor A = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor B = RandomVariate.of(NormalDistribution.standard(), 10, 20, 5);
    Tensor expect = Transpose.of(A).dot(B);
    // IO.println(Dimensions.of(expect));
    Tensor result = TransposeMatrixDot.of(A, B);
    // IO.println(Dimensions.of(result));
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  void testVectorFail() {
    Tensor A = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor B = RandomVariate.of(NormalDistribution.standard(), 10, 20, 5);
    assertThrows(Exception.class, () -> TransposeMatrixDot.of(A, B));
  }
}

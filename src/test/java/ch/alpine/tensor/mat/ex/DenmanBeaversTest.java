// code by jph
package ch.alpine.tensor.mat.ex;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class DenmanBeaversTest {
  @Test
  void testSimple() {
    RandomGenerator random = new Random(3);
    Distribution distribution = NormalDistribution.standard();
    for (int n = 2; n < 6; ++n) {
      Tensor _a = RandomVariate.of(distribution, random, n, n);
      Tensor a = MatrixPower.of(_a, 2);
      MatrixSqrt matrixSqrt = new DenmanBeavers(a);
      Chop._08.requireClose(a, MatrixPower.of(matrixSqrt.sqrt(), 2));
      Tensor e = matrixSqrt.sqrt().dot(matrixSqrt.sqrt_inverse());
      Chop._08.requireClose(e, IdentityMatrix.of(n));
      Chop._08.requireClose( //
          MatrixPower.of(matrixSqrt.sqrt(), -1), //
          matrixSqrt.sqrt_inverse());
    }
  }
}

// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.fft.FourierDCT;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.pow.Sqrt;

class ChebyshevNodesTest {
  @RepeatedTest(6)
  void testSymmetric(RepetitionInfo repetitionInfo) {
    Tensor matrix = ChebyshevNodes._0.matrix(repetitionInfo.getCurrentRepetition());
    SymmetricMatrixQ.INSTANCE.require(matrix);
  }

  @RepeatedTest(10)
  void testDCT2(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    final Tensor m1 = ChebyshevNodes._1.matrix(n);
    final Tensor m2 = FourierDCT._2.matrix(n);
    final Scalar ratio = m1.Get(0, 0).divide(m2.Get(0, 0));
    final Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
    Tolerance.CHOP.requireClose(ratio, scalar.reciprocal());
    Tolerance.CHOP.requireClose(m1, m2.multiply(ratio));
    final Tensor m3 = FourierDCT._3.matrix(n);
    Tolerance.CHOP.requireClose(IdentityMatrix.of(n), m3.dot(m2));
    final Tensor m1inv = m3.multiply(scalar);
    Tolerance.CHOP.requireClose(IdentityMatrix.of(n), m1.dot(m1inv));
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> ChebyshevNodes._1.of(0, 0));
    assertThrows(Exception.class, () -> ChebyshevNodes._1.of(3, 4));
    assertThrows(Exception.class, () -> ChebyshevNodes._1.of(3, -1));
  }
}

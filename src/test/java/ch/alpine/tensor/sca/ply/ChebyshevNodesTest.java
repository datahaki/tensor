// code by jph
package ch.alpine.tensor.sca.ply;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.fft.FourierDCTMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.pow.Sqrt;

class ChebyshevNodesTest {
  @RepeatedTest(6)
  void testSymmetric(RepetitionInfo repetitionInfo) {
    Tensor matrix = ChebyshevNodes._0.matrix(repetitionInfo.getCurrentRepetition());
    SymmetricMatrixQ.require(matrix);
  }

  @RepeatedTest(10)
  void testDCT2(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor m1 = ChebyshevNodes._1.matrix(n);
    Tensor m2 = FourierDCTMatrix._2.of(n);
    Scalar ratio = m1.Get(0, 0).divide(m2.Get(0, 0));
    Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
    Tolerance.CHOP.requireClose(ratio, scalar.reciprocal());
    m2 = m2.multiply(ratio);
    Tolerance.CHOP.requireClose(m1, m2);
  }
}

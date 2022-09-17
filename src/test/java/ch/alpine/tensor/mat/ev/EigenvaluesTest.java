// code by jph
package ch.alpine.tensor.mat.ev;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.ply.ChebyshevNodes;

class EigenvaluesTest {
  @Test
  void testSimple() {
    Tensor matrix = ChebyshevNodes._0.matrix(32);
    Tensor vector = Eigenvalues.ofSymmetric(matrix);
    Tensor lo = vector.extract(1, 15);
    Tensor hi = vector.extract(17, 31);
    Tolerance.CHOP.requireClose(lo, ConstantArray.of(RealScalar.of(+4), lo.length()));
    Tolerance.CHOP.requireClose(hi, ConstantArray.of(RealScalar.of(-4), hi.length()));
  }
}

// code by jph
package ch.alpine.tensor.mat.ev;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.mat.HermitianMatrixQ;
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

  @Test
  void testHermitian() {
    Tensor matrix = ChebyshevNodes._0.matrix(32);
    Tensor vector = Eigenvalues.ofHermitian(matrix);
    Tensor lo = vector.extract(1, 15);
    Tensor hi = vector.extract(17, 31);
    Tolerance.CHOP.requireClose(lo, ConstantArray.of(RealScalar.of(+4), lo.length()));
    Tolerance.CHOP.requireClose(hi, ConstantArray.of(RealScalar.of(-4), hi.length()));
  }

  @Test
  void testComplex() {
    Tensor matrix = Tensors.fromString("{{2, 3-3*I}, {3+3*I, 5}}");
    HermitianMatrixQ.require(matrix);
    Tensor vector = Eigenvalues.ofHermitian(matrix);
    Tolerance.CHOP.requireClose(vector, Tensors.vector(8, -1));
  }
}

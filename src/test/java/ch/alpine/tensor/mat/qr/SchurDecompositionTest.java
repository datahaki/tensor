// code by jph
package ch.alpine.tensor.mat.qr;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.sca.Chop;

class SchurDecompositionTest {
  @Test
  void testSimple() {
    Tensor matrix = Tensors.matrixDouble(new double[][] { { 3.7, 0.8, 0.1 }, { .2, 5, .3 }, { .1, 0, 4.3 } });
    SchurDecomposition schurDecomposition = SchurDecomposition.of(matrix);
    Tensor r = schurDecomposition.getR();
    Tensor qn = schurDecomposition.getQ();
    Chop._08.requireClose(Dot.of(Inverse.of(qn), matrix, qn), r);
  }
}

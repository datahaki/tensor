// code by jph
package ch.alpine.tensor.mat.qr;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.sca.Chop;

class HessenbergDecompositionTest {
  @Test
  void test() {
    Tensor matrix = Tensors.fromString("{{1., 2., 3., 4.}, {5., 6., 7., 8.}, {9., 10., 11., 12.}, {13., 14., 15., 16.}}");
    HessenbergDecomposition hd = HessenbergDecomposition.of(matrix);
    Tensor r = hd.getR();
    Tensor qn = hd.getQ();
    Chop._08.requireClose(Dot.of(Inverse.of(qn), matrix, qn), r);
    // TODO TENSOR not consistent with Mathematica
    // System.out.println(Pretty.of(r));
  }
}

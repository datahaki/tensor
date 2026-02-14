// code by jph
package ch.alpine.tensor.mat.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;

class LinearSubspaceMemberQTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5 })
  void testAntiSymm(int n) {
    LinearSubspace linearSubspace = LinearSubspace.of(AntisymmetricMatrixQ.INSTANCE::defect, n, n);
    ZeroDefectArrayQ zeroDefectArrayQ = LinearSubspaceMemberQ.of(linearSubspace, Chop.NONE);
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-20, 20), n, n);
    matrix = TensorWedge.of(matrix);
    zeroDefectArrayQ.require(matrix);
  }
}

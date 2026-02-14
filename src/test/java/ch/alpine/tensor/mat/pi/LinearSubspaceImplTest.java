// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class LinearSubspaceImplTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 8 })
  void testAntiSymm(int n) {
    LinearSubspace linearSubspace = LinearSubspace.of(AntisymmetricMatrixQ.INSTANCE::defect, n, n);
    int dimensions = linearSubspace.dimensions();
    assertEquals(dimensions, n * (n - 1) / 2);
    ExactTensorQ.require(linearSubspace.basis());
    Tensor weights = RandomVariate.of(NormalDistribution.standard(), dimensions);
    linearSubspace.apply(weights);
  }
}

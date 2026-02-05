package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.RotateLeft;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class LinearSubspaceTest {
  @Test
  void testVector() {
    LinearSubspace linearSubspace = LinearSubspace.of(v -> Tensors.vector(1, 0, 0).dot(v), 3);
    assertEquals(linearSubspace.basis(), Tensors.fromString("{{0, 1, 0}, {0, 0, 1}}"));
    Tensor w = linearSubspace.projection(Tensors.vector(3, 4, 5));
    assertEquals(w, Tensors.vector(0, 4, 5));
    assertTrue(linearSubspace.toString().startsWith("LinearSubspace["));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 8 })
  void testAntiSymm(int n) {
    LinearSubspace linearSubspace = LinearSubspace.of(AntisymmetricMatrixQ.INSTANCE::defect, n, n);
    int dimensions = linearSubspace.dimensions();
    assertEquals(dimensions, n * (n - 1) / 2);
    ExactTensorQ.require(linearSubspace.basis());
    Tensor weights = RandomVariate.of(NormalDistribution.standard(), dimensions);
    linearSubspace.apply(weights);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> LinearSubspace.of(v -> RotateLeft.of(v, 1), 3));
  }
}

// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;

class InfluenceMatrixQTest {
  @Test
  void testSimple() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {2, 3}}");
    assertFalse(InfluenceMatrixQ.of(matrix));
    assertThrows(Throw.class, () -> InfluenceMatrixQ.require(matrix));
  }

  @Test
  void testChop() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {2, 3}}");
    assertFalse(InfluenceMatrixQ.of(matrix, Tolerance.CHOP));
    assertThrows(Throw.class, () -> InfluenceMatrixQ.require(matrix, Tolerance.CHOP));
  }

  @Test
  void testProblem0() {
    Tensor design = Tensors.fromString( //
        "{{-304[m], -144[m], 16[m]}, {-19[m], -9[m], 1[m]}, {285[m], 135[m], -15[m]}, {-152[m], -72[m], 8[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    InfluenceMatrixQ.require(influenceMatrix.matrix());
  }

  @Test
  void testProblem1() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], -228[m], 0[m]}, {0[m], -266[m], 0[m]}, {0[m], 342[m], 0[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    InfluenceMatrixQ.require(influenceMatrix.matrix());
  }

  @Test
  void testProblem2() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], 30[m], -285[m]}, {0[m], -32[m], 304[m]}, {0[m], 10[m], -95[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    InfluenceMatrixQ.require(influenceMatrix.matrix());
  }
}

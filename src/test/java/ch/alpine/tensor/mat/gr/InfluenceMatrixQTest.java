// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.HermitianMatrixQ;

class InfluenceMatrixQTest {
  @Test
  void testSimple() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {2, 3}}");
    assertFalse(InfluenceMatrixQ.INSTANCE.test(matrix));
    assertThrows(Throw.class, () -> InfluenceMatrixQ.INSTANCE.require(matrix));
  }

  @Test
  void testIdempotent() {
    Tensor matrix = Tensors.fromString("{{1,1},{0,0}}");
    IdempotentMatrixQ.INSTANCE.require(matrix);
    assertFalse(HermitianMatrixQ.INSTANCE.test(matrix));
    assertFalse(InfluenceMatrixQ.INSTANCE.test(matrix));
  }

  @Test
  void testChop() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {2, 3}}");
    assertFalse(InfluenceMatrixQ.INSTANCE.test(matrix));
    assertThrows(Throw.class, () -> InfluenceMatrixQ.INSTANCE.require(matrix));
  }

  @Test
  void testProblem0() {
    Tensor design = Tensors.fromString( //
        "{{-304[m], -144[m], 16[m]}, {-19[m], -9[m], 1[m]}, {285[m], 135[m], -15[m]}, {-152[m], -72[m], 8[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    Tensor tensor = InfluenceMatrixQ.INSTANCE.require(influenceMatrix.matrix());
    assertEquals(tensor, influenceMatrix.matrix());
  }

  @Test
  void testProblem1() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], -228[m], 0[m]}, {0[m], -266[m], 0[m]}, {0[m], 342[m], 0[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    InfluenceMatrixQ.INSTANCE.require(influenceMatrix.matrix());
  }

  @Test
  void testProblem2() {
    Tensor design = Tensors.fromString( //
        "{{0[m], 0[m], 0[m]}, {0[m], 30[m], -285[m]}, {0[m], -32[m], 304[m]}, {0[m], 10[m], -95[m]}}");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    InfluenceMatrixQ.INSTANCE.require(influenceMatrix.matrix());
  }
}

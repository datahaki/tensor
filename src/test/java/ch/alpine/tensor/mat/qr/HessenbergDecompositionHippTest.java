// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import test.wrap.HessenbergDecompositionQ;

class HessenbergDecompositionHippTest {
  private static HessenbergDecomposition _check(Tensor matrix) {
    HessenbergDecomposition hessenbergDecomposition = new HessenbergDecompositionHipp(SquareMatrixQ.INSTANCE.requireMember(matrix));
    new HessenbergDecompositionQ(matrix).check(hessenbergDecomposition);
    return hessenbergDecomposition;
  }

  @Test
  void testMathematicaEx() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}}");
    HessenbergDecomposition hessenbergDecomposition = Serialization.copy(_check(matrix));
    assertTrue(hessenbergDecomposition.toString().startsWith("HessenbergDecomposition["));
  }

  @Test
  void testRandom2x2() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 2, 2);
    HessenbergDecomposition hessenbergDecomposition = _check(matrix);
    assertEquals(hessenbergDecomposition.getH(), matrix);
    assertEquals(hessenbergDecomposition.getUnitary(), IdentityMatrix.of(2));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    _check(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testZeros(int n) {
    Tensor matrix = Array.zeros(n, n);
    _check(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m")), n, n);
    _check(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomExact(int n) {
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-1, 2), n, n);
    _check(matrix);
  }
}

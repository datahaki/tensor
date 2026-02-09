// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class RapolyTest {
  @Test
  void testRapoly1() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {0, 0}}");
    matrix.set(Rapoly.of(Tensors.vector(0, -1)), 0, 0);
    matrix.set(Rapoly.of(Tensors.vector(0, -1)), 1, 1);
    Rapoly rapoly = (Rapoly) Det.withoutDivision(matrix);
    assertEquals(rapoly.polynomial().roots(), Array.zeros(2));
  }

  @Test
  void testRapoly2() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {2, 0}}");
    matrix.set(Rapoly.of(Tensors.vector(3, -1)), 0, 0);
    matrix.set(Rapoly.of(Tensors.vector(4, -1)), 1, 1);
    Rapoly rapoly = (Rapoly) Det.withoutDivision(matrix);
    Tensor roots = rapoly.polynomial().roots();
    ExactTensorQ.require(roots);
    assertEquals(Tensors.vector(2, 5), roots);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testRotation(int n) {
    Tensor matrix = TensorWedge.of(RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), n, n));
    matrix = MatrixExp.of(matrix);
    Tensor res = matrix.subtract(DiagonalMatrix.of(n, Rapoly.of(Tensors.vector(0, 1))));
    Rapoly rapoly = (Rapoly) Det.withoutDivision(res);
    rapoly.polynomial().roots().maps(Tolerance.CHOP);
  }
}

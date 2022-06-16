// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.itp.Fit;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class VandermondeSolveTest {
  @Test
  void testSimple() {
    Tensor x = Tensors.vector(2, 3);
    Tensor q = Tensors.vector(4, 7);
    Tensor ref = LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q);
    Tensor cmp = VandermondeSolve.of(x, q);
    ExactTensorQ.require(cmp);
    assertEquals(ref, cmp);
    Fit.polynomial(x, q, 1);
  }

  @Test
  void testMixedUnits() {
    for (int degree = 0; degree <= 5; ++degree) {
      Tensor x = Tensors.fromString("{100[K], 110.0[K], 120[K], 133[K], 140[K], 150[K]}").extract(0, degree + 1);
      Tensor q = Tensors.fromString("{10[], 20[K], 22[K^2], 23[K^3], 25[K^4], 26.0[K^5]}").extract(0, degree + 1);
      Tensor ref = LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q);
      Tensor cmp = VandermondeSolve.of(x, q);
      Chop._04.requireClose(ref, cmp);
    }
  }

  @Test
  void testNumeric() {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.standard();
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(distribution, random, n);
      Tensor q = RandomVariate.of(distribution, random, n);
      Tensor ref = LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q);
      Tensor cmp = VandermondeSolve.of(x, q);
      Chop._08.requireClose(ref, cmp);
    }
  }

  @Test
  void testSingularFail() {
    Tensor x = Tensors.vector(2, 3, 2);
    Tensor q = Tensors.vector(4, 7, 6);
    assertThrows(TensorRuntimeException.class, () -> VandermondeSolve.of(x, q));
  }

  @Test
  void testLengthFail() {
    Tensor x = Tensors.vector(2, 3);
    Tensor q = Tensors.vector(4, 7, 6);
    assertThrows(TensorRuntimeException.class, () -> VandermondeSolve.of(x, q));
  }

  @Test
  void testGaussScalar() {
    int prime = 7817;
    Tensor x = Tensors.of( //
        GaussScalar.of(1210, prime), //
        GaussScalar.of(1343, prime), //
        GaussScalar.of(3318, prime));
    Tensor q = Tensors.of( //
        GaussScalar.of(1, prime), //
        GaussScalar.of(3, prime), //
        GaussScalar.of(7, prime));
    Tensor matrix = VandermondeMatrix.of(x);
    assertEquals(Det.of(matrix), GaussScalar.of(1705, prime));
    Tensor ref = LinearSolve.of(Transpose.of(matrix), q, Pivots.FIRST_NON_ZERO);
    Tensor cmp = VandermondeSolve.of(x, q);
    assertEquals(ref, cmp);
  }

  @Test
  void testEmptyFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> VandermondeSolve.of(Tensors.empty(), Tensors.empty()));
  }
}

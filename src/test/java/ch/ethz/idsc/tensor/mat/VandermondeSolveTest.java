// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class VandermondeSolveTest extends TestCase {
  public void testSimple() {
    Tensor x = Tensors.vector(2, 3);
    Tensor q = Tensors.vector(4, 7);
    Tensor ref = LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q);
    Tensor cmp = VandermondeSolve.of(x, q);
    ExactTensorQ.require(cmp);
    assertEquals(ref, cmp);
  }

  public void testNumeric() {
    Distribution distribution = NormalDistribution.standard();
    int fails = 0;
    for (int n = 1; n < 10; ++n)
      try {
        Tensor x = RandomVariate.of(distribution, n);
        Tensor q = RandomVariate.of(distribution, n);
        Tensor ref = LinearSolve.of(Transpose.of(VandermondeMatrix.of(x)), q);
        Tensor cmp = VandermondeSolve.of(x, q);
        Chop._04.requireClose(ref, cmp);
      } catch (Exception exception) {
        ++fails;
      }
    assertTrue(fails <= 2);
  }

  public void testSingularFail() {
    Tensor x = Tensors.vector(2, 3, 2);
    Tensor q = Tensors.vector(4, 7, 6);
    AssertFail.of(() -> VandermondeSolve.of(x, q));
  }

  public void testLengthFail() {
    Tensor x = Tensors.vector(2, 3);
    Tensor q = Tensors.vector(4, 7, 6);
    AssertFail.of(() -> VandermondeSolve.of(x, q));
  }

  public void testGaussScalar() {
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

  public void testEmptyFail() {
    AssertFail.of(() -> VandermondeSolve.of(Tensors.empty(), Tensors.empty()));
  }
}

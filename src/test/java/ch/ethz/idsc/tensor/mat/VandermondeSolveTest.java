// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class VandermondeSolveTest extends TestCase {
  public void testSimple() {
    Tensor x = Tensors.vector(2, 3);
    Tensor q = Tensors.vector(4, 7);
    Tensor ref = LinearSolve.of(VandermondeMatrix.of(x), q);
    Tensor cmp = VandermondeSolve.of(x, q);
    ExactTensorQ.require(cmp);
    assertEquals(ref, cmp);
  }

  public void testNumeric() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(distribution, n);
      Tensor q = RandomVariate.of(distribution, n);
      Tensor ref = LinearSolve.of(VandermondeMatrix.of(x), q);
      Tensor cmp = VandermondeSolve.of(x, q);
      Chop._04.requireClose(ref, cmp);
    }
  }

  public void testSingular() {
    Tensor x = Tensors.vector(2, 3, 2);
    Tensor q = Tensors.vector(4, 7, 6);
    try {
      VandermondeSolve.of(x, q);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEmptyFail() {
    try {
      VandermondeSolve.of(Tensors.empty(), Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}

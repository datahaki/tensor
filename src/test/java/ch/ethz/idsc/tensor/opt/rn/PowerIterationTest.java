// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PowerIterationTest extends TestCase {
  public void testSymmetric() {
    Tensor matrix = Tensors.fromString("{{2, 3, 0, 1}, {3, 1, 7, 5}, {0, 7, 10, 9}, {1, 5, 9, 13}}");
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor v = eigensystem.vectors().get(0).unmodifiable();
    Tensor x = PowerIteration.of(matrix).get();
    Chop._12.requireClose(Abs.of(x.dot(v)), RealScalar.ONE);
  }

  public void testNegative() {
    Tensor matrix = Tensors.fromString("{{-1, 0}, {0, 0}}");
    Tensor x = PowerIteration.of(matrix).get();
    assertEquals(Abs.of(x.Get(0)), RealScalar.ONE);
    assertEquals(Abs.of(x.Get(1)), RealScalar.ZERO);
  }

  public void testScalar() {
    Tensor matrix = Tensors.fromString("{{2}}");
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor v = eigensystem.vectors().get(0).unmodifiable();
    assertEquals(Abs.of(v.Get(0)), RealScalar.ONE);
    Tensor x = PowerIteration.of(matrix).get();
    assertEquals(Abs.of(x.Get(0)), RealScalar.ONE);
  }

  public void testZerosFail() {
    AssertFail.of(() -> PowerIteration.of(Array.zeros(3, 3)));
  }

  public void testVectorFail() {
    AssertFail.of(() -> PowerIteration.of(Tensors.vector(1, 2, 3)));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> PowerIteration.of(HilbertMatrix.of(4, 3)));
    AssertFail.of(() -> PowerIteration.of(HilbertMatrix.of(3, 4)));
  }
}

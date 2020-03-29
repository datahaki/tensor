// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import junit.framework.TestCase;

public class HodgeDualTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor matrix = HodgeDual.of(vector, 3);
    assertEquals(matrix, Tensors.fromString("{{0, 3, -2}, {-3, 0, 1}, {2, -1, 0}}"));
    Tensor checks = HodgeDual.of(matrix, 3);
    assertEquals(checks, vector);
  }

  public void testScalar2() {
    Tensor matrix = HodgeDual.of(RealScalar.of(3), 2);
    assertEquals(matrix, Tensors.fromString("{{0, 3}, {-3, 0}}"));
  }

  public void testScalar3() {
    Tensor matrix = HodgeDual.of(RealScalar.of(5), 3);
    assertEquals(matrix, Tensors.fromString("{{{0, 0, 0}, {0, 0, 5}, {0, -5, 0}}, {{0, 0, -5}, {0, 0, 0}, {5, 0, 0}}, {{0, 5, 0}, {-5, 0, 0}, {0, 0, 0}}}"));
  }

  public void testLeviCivitaTensor() {
    for (int d = 1; d < 5; ++d)
      assertEquals(LeviCivitaTensor.of(d), HodgeDual.of(RealScalar.ONE, d));
    for (int d = 1; d < 5; ++d)
      assertEquals(RealScalar.ONE, HodgeDual.of(LeviCivitaTensor.of(d), d));
  }

  public void testMismatchFail() {
    Tensor vector = Tensors.vector(1, 2, 3);
    try {
      HodgeDual.of(vector, 2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonArrayFail() {
    Tensor vector = Tensors.fromString("{{1, 2}, {3, 4, 5}}");
    try {
      HodgeDual.of(vector, 2);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      HodgeDual.of(vector, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonRegularFail() {
    Tensor vector = Array.zeros(2, 3);
    try {
      HodgeDual.of(vector, 2);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      HodgeDual.of(vector, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeDimFail() {
    try {
      HodgeDual.of(RealScalar.ONE, -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}

// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RiffleTest extends TestCase {
  public void testSimple() {
    Tensor vector = Riffle.of(Tensors.vector(1, 2, 3, 4, 5), RealScalar.ZERO);
    assertEquals(vector, Tensors.vector(1, 0, 2, 0, 3, 0, 4, 0, 5));
  }

  public void testEmpty() {
    Tensor vector = Riffle.of(Tensors.empty(), RealScalar.ZERO);
    assertTrue(Tensors.isEmpty(vector));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Riffle.of(RealScalar.ZERO, RealScalar.ZERO));
  }
}

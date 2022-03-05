// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ScalarQTest extends TestCase {
  /** equivalent to the predicate {@code tensor.length() == Scalar.LENGTH}
   * 
   * @param tensor
   * @return true if given tensor is instance of {@link Scalar} */
  public static boolean of(Tensor tensor) {
    return tensor instanceof Scalar;
  }

  public void testScalar() {
    assertTrue(of(Quantity.of(3, "m")));
    assertTrue(of(GaussScalar.of(3, 11)));
    assertTrue(of(StringScalar.of("IDSC")));
  }

  public void testVector() {
    assertFalse(of(Tensors.vector(1, 2, 3)));
  }

  public void testThenThrow() {
    ScalarQ.thenThrow(Tensors.vector(1, 2, 3));
    AssertFail.of(() -> ScalarQ.thenThrow(RealScalar.ONE));
  }
}

// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ConjugateTest extends TestCase {
  public void testQuantity1() {
    Scalar scalar = Scalars.fromString("0+0*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    assertEquals(Real.of(scalar).toString(), "0[m*s]");
    assertEquals(Imag.of(scalar).toString(), "0[m*s]");
    assertEquals(Conjugate.of(scalar).toString(), "0[m*s]");
  }

  public void testQuantity2() {
    Scalar scalar = Scalars.fromString("3+5*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    assertEquals(Real.of(scalar), Quantity.of(3, "m*s"));
    assertEquals(Imag.of(scalar), Quantity.of(5, "m*s"));
    assertEquals(Conjugate.of(scalar), Scalars.fromString("3-5*I[m*s]"));
  }

  public void testFail() {
    AssertFail.of(() -> Conjugate.of(StringScalar.of("asd")));
  }
}

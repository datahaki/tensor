// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class ConjugateTest {
  @Test
  public void testQuantity1() {
    Scalar scalar = Scalars.fromString("0+0*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    assertEquals(Real.of(scalar).toString(), "0[m*s]");
    assertEquals(Imag.of(scalar).toString(), "0[m*s]");
    assertEquals(Conjugate.of(scalar).toString(), "0[m*s]");
  }

  @Test
  public void testQuantity2() {
    Scalar scalar = Scalars.fromString("3+5*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    assertEquals(Real.of(scalar), Quantity.of(3, "m*s"));
    assertEquals(Imag.of(scalar), Quantity.of(5, "m*s"));
    assertEquals(Conjugate.of(scalar), Scalars.fromString("3-5*I[m*s]"));
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> Conjugate.of(StringScalar.of("asd")));
  }
}
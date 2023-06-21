// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;

class ConjugateTest {
  @Test
  void testQuantity1() {
    Scalar scalar = Scalars.fromString("0+0*I[m*s]");
    assertInstanceOf(Quantity.class, scalar);
    assertEquals(Re.FUNCTION.apply(scalar).toString(), "0[m*s]");
    assertEquals(Im.FUNCTION.apply(scalar).toString(), "0[m*s]");
    assertEquals(Conjugate.FUNCTION.apply(scalar).toString(), "0[m*s]");
  }

  @Test
  void testQuantity2() {
    Scalar scalar = Scalars.fromString("3+5*I[m*s]");
    assertInstanceOf(Quantity.class, scalar);
    assertEquals(Re.FUNCTION.apply(scalar), Quantity.of(3, "m*s"));
    assertEquals(Im.FUNCTION.apply(scalar), Quantity.of(5, "m*s"));
    assertEquals(Conjugate.FUNCTION.apply(scalar), Scalars.fromString("3-5*I[m*s]"));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> Conjugate.FUNCTION.apply(StringScalar.of("asd")));
  }
}

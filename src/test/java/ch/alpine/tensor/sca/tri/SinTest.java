// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;

class SinTest {
  @Test
  public void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Sin.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.sin(2));
    assertEquals(c, Sin.of(i));
    assertEquals(c, s);
  }

  @Test
  public void testComplex() {
    Scalar c = Sin.FUNCTION.apply(ComplexScalar.of(2, 3.));
    // 9.1544991469114295735 - 4.1689069599665643508 I
    Scalar s = Scalars.fromString("9.15449914691143-4.168906959966565*I");
    assertEquals(c, s);
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Sin.of(Quantity.of(1, "deg")));
  }

  @Test
  public void testStringScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Sin.of(StringScalar.of("some")));
  }
}

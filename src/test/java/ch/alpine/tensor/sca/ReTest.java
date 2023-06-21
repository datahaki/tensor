// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.DateTime;

class ReTest {
  @Test
  void testReal() {
    Scalar scalar = Scalars.fromString("11");
    assertEquals(Re.FUNCTION.apply(scalar), RealScalar.of(11));
    assertEquals(Im.FUNCTION.apply(scalar), RealScalar.of(0));
    assertEquals(Re.FUNCTION.apply(scalar), RealScalar.of(11));
    assertEquals(Im.FUNCTION.apply(scalar), RealScalar.of(0));
  }

  @Test
  void testComplex() {
    Scalar scalar = ComplexScalar.of(11, 3.5);
    assertEquals(Re.FUNCTION.apply(scalar), RealScalar.of(11));
    assertEquals(Im.FUNCTION.apply(scalar), RealScalar.of(3.5));
    assertEquals(Re.FUNCTION.apply(scalar), RealScalar.of(11));
    assertEquals(Im.FUNCTION.apply(scalar), RealScalar.of(3.5));
  }

  @Test
  void testStringFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Re.FUNCTION.apply(scalar));
  }

  @Test
  void testDateTimeFail() {
    Scalar scalar = DateTime.now();
    assertThrows(Throw.class, () -> Re.FUNCTION.apply(scalar));
  }
}

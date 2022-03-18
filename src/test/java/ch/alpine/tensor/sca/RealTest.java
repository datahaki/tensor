// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.usr.AssertFail;

public class RealTest {
  @Test
  public void testReal() {
    Scalar scalar = Scalars.fromString("11");
    assertEquals(Real.FUNCTION.apply(scalar), RealScalar.of(11));
    assertEquals(Imag.FUNCTION.apply(scalar), RealScalar.of(0));
    assertEquals(Real.of(scalar), RealScalar.of(11));
    assertEquals(Imag.of(scalar), RealScalar.of(0));
  }

  @Test
  public void testComplex() {
    Scalar scalar = Scalars.fromString("11+3.5*I");
    assertEquals(Real.FUNCTION.apply(scalar), RealScalar.of(11));
    assertEquals(Imag.FUNCTION.apply(scalar), RealScalar.of(3.5));
    assertEquals(Real.of(scalar), RealScalar.of(11));
    assertEquals(Imag.of(scalar), RealScalar.of(3.5));
  }

  @Test
  public void testFail() {
    Scalar scalar = StringScalar.of("string");
    AssertFail.of(() -> Real.of(scalar));
  }
}
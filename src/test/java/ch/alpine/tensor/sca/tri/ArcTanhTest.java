// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.jet.Around;
import ch.alpine.tensor.usr.AssertFail;

public class ArcTanhTest {
  @Test
  public void testReal() {
    Scalar scalar = ArcTanh.of(RealScalar.of(0.5));
    assertEquals(scalar, RealScalar.of(0.5493061443340548));
  }

  @Test
  public void testComplex() {
    Scalar scalar = ArcTanh.of(ComplexScalar.of(5, -9));
    // 0.0468657 - 1.48591 I
    assertEquals(scalar, ComplexScalar.of(0.04686573907359337, -1.4859071898107274));
  }

  @Test
  public void testFail() {
    Scalar scalar = Around.of(2, 3);
    AssertFail.of(() -> ArcTanh.FUNCTION.apply(scalar));
  }
}

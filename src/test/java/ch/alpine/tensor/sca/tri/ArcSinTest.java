// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Chop;

class ArcSinTest {
  @Test
  void testReal() {
    assertEquals(ArcSin.FUNCTION.apply(Scalars.fromString("1")), RealScalar.of(Math.asin(1)));
    assertEquals(ArcSin.FUNCTION.apply(Scalars.fromString("-1")), RealScalar.of(Math.asin(-1)));
  }

  @Test
  void testRealOutside() {
    Scalar s = RealScalar.of(3);
    Scalar r = ArcSin.FUNCTION.apply(s);
    Chop._14.requireClose(r, ComplexScalar.of(1.5707963267948966, -1.762747174039086));
  }

  @Test
  void testRealOutsideNeg() {
    Scalar s = RealScalar.of(-3);
    Scalar r = ArcSin.FUNCTION.apply(s);
    assertEquals(r, ArcSin.FUNCTION.apply(s));
    Chop._14.requireClose(r, ComplexScalar.of(-1.5707963267948966, +1.7627471740390872));
  }

  @Test
  void testComplex() {
    Scalar s = ComplexScalar.of(5, -7);
    Scalar r = ArcSin.FUNCTION.apply(s);
    assertEquals(r, ArcSin.FUNCTION.apply(s));
    Chop._14.requireClose(r, ComplexScalar.of(0.6170642966759935, -2.8462888282083862));
  }
}

// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class ArcTanTest extends TestCase {
  public void testReal() {
    Scalar s = RealScalar.of(-3);
    Scalar r = ArcTan.FUNCTION.apply(s);
    // System.out.println(r);
    assertEquals(r, ArcTan.of(s));
    // -1.5707963267948966192 + 1.7627471740390860505 I
    assertEquals(r, Scalars.fromString("-1.2490457723982544"));
  }

  public void testRealZero() {
    Scalar r = ArcTan.of(RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(r, RealScalar.ZERO);
  }

  public void testComplexReal() {
    Scalar r = ArcTan.of(ComplexScalar.of(2, 3), RealScalar.of(12));
    // 1.39519 - 0.247768 I
    assertEquals(r, Scalars.fromString("1.3951860877095887-0.24776768676598088*I"));
  }

  public void testComplex() {
    Scalar s = ComplexScalar.of(5, -7);
    Scalar r = ArcTan.FUNCTION.apply(s);
    assertEquals(r, ArcTan.of(s));
    // 1.50273 - 0.0944406 I
    assertEquals(r, Scalars.fromString("1.502726846368326-0.09444062638970714*I"));
  }

  public void testComplex2() {
    Scalar x = ComplexScalar.of(4, -1);
    Scalar y = ComplexScalar.of(1, 2);
    Scalar r = ArcTan.of(x, y);
    // 0.160875 + 0.575646 I
    assertEquals(r, Scalars.fromString("0.1608752771983211+0.5756462732485114*I"));
  }

  public void testComplexZeroP() {
    Scalar x = RealScalar.ZERO;
    Scalar y = ComplexScalar.of(1, 2);
    Scalar r = ArcTan.of(x, y);
    assertEquals(r, DoubleScalar.of(Math.PI / 2));
  }

  public void testComplexZeroN() {
    Scalar x = RealScalar.ZERO;
    Scalar y = ComplexScalar.of(-1, 2);
    Scalar r = ArcTan.of(x, y);
    assertEquals(r, DoubleScalar.of(-Math.PI / 2));
  }

  public void testCornerCases() {
    assertEquals(ArcTan.of(RealScalar.of(-5), RealScalar.ZERO), DoubleScalar.of(Math.PI));
    assertEquals(ArcTan.of(RealScalar.ZERO, RealScalar.ZERO), RealScalar.ZERO);
  }
}

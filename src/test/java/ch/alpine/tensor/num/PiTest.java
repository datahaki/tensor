// code by jph
package ch.alpine.tensor.num;

import java.util.Random;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Sin;
import junit.framework.TestCase;

public class PiTest extends TestCase {
  public void testTrigonometry() {
    Scalar pi = Pi.in(100);
    Scalar sin_pi = Sin.of(pi);
    Scalar cos_pi = Cos.of(pi);
    Chop._40.requireClose(sin_pi, RealScalar.ZERO);
    Chop._40.requireClose(cos_pi, RealScalar.ONE.negate());
  }

  public void testTwo2() {
    assertEquals(Pi.HALF.multiply(RealScalar.of(2)), Pi.VALUE);
    assertEquals(Pi.VALUE.divide(RealScalar.of(2)), Pi.HALF);
  }

  public void testTwo3() {
    assertEquals(Pi.TWO.divide(RealScalar.of(2)), Pi.VALUE);
    assertEquals(Pi.VALUE.multiply(RealScalar.of(2)), Pi.TWO);
  }

  public void testTwo4() {
    assertEquals(Pi.HALF.multiply(RealScalar.of(4)), Pi.TWO);
    assertEquals(Pi.TWO.divide(RealScalar.of(4)), Pi.HALF);
  }

  public void testString() {
    Scalar pi = Pi.in(110);
    assertTrue(pi instanceof DecimalScalar);
    String PI99 = "3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706";
    assertTrue(pi.toString().startsWith(PI99));
  }

  public void test500() {
    Random random = new Random();
    Scalar pi = Pi.in(400 + random.nextInt(200));
    assertTrue(pi instanceof DecimalScalar);
    double value = pi.add(pi).number().doubleValue();
    assertEquals(value, 2 * Math.PI);
  }

  public void testDoublePrecision() {
    assertEquals(Pi.VALUE.number().doubleValue(), Math.PI);
    assertEquals(Pi.HALF.number().doubleValue(), Math.PI * 0.5);
    assertEquals(Pi.TWO.number().doubleValue(), Math.PI / 0.5);
    assertEquals(Pi.TWO.number().doubleValue(), Math.PI + Math.PI);
  }
}

// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

public class PiTest {
  @Test
  public void testTrigonometry() {
    Scalar pi = Pi.in(100);
    Scalar sin_pi = Sin.of(pi);
    Scalar cos_pi = Cos.of(pi);
    Chop._40.requireClose(sin_pi, RealScalar.ZERO);
    Chop._40.requireClose(cos_pi, RealScalar.ONE.negate());
  }

  @Test
  public void testTwo2() {
    assertEquals(Pi.HALF.multiply(RealScalar.of(2)), Pi.VALUE);
    assertEquals(Pi.VALUE.divide(RealScalar.of(2)), Pi.HALF);
  }

  @Test
  public void testTwo3() {
    assertEquals(Pi.TWO.divide(RealScalar.of(2)), Pi.VALUE);
    assertEquals(Pi.VALUE.multiply(RealScalar.of(2)), Pi.TWO);
  }

  @Test
  public void testTwo4() {
    assertEquals(Pi.HALF.multiply(RealScalar.of(4)), Pi.TWO);
    assertEquals(Pi.TWO.divide(RealScalar.of(4)), Pi.HALF);
  }

  @Test
  public void testString() {
    Scalar pi = Pi.in(110);
    assertInstanceOf(DecimalScalar.class, pi);
    String PI99 = "3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706";
    assertTrue(pi.toString().startsWith(PI99));
  }

  @Test
  public void test500() {
    Random random = new Random();
    Scalar pi = Pi.in(400 + random.nextInt(200));
    assertInstanceOf(DecimalScalar.class, pi);
    double value = pi.add(pi).number().doubleValue();
    assertEquals(value, 2 * Math.PI);
  }

  @Test
  public void testDoublePrecision() {
    assertEquals(Pi.VALUE.number().doubleValue(), Math.PI);
    assertEquals(Pi.HALF.number().doubleValue(), Math.PI * 0.5);
    assertEquals(Pi.TWO.number().doubleValue(), Math.PI / 0.5);
    assertEquals(Pi.TWO.number().doubleValue(), Math.PI + Math.PI);
  }
}

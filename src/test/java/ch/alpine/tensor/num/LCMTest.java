// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;

class LCMTest {
  @Test
  void testZero() {
    assertEquals(LCM.of(RealScalar.ZERO, RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(LCM.of(RealScalar.of(3), RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(LCM.of(RealScalar.ZERO, RealScalar.of(3)), RealScalar.ZERO);
  }

  @Test
  void testExamples() {
    assertEquals(LCM.of(RealScalar.of(+123), RealScalar.of(+345)), RealScalar.of(14145));
    assertEquals(LCM.of(RealScalar.of(+123 * 5), RealScalar.of(345)), RealScalar.of(14145));
    assertEquals(LCM.of(RealScalar.of(-123), RealScalar.of(+345)), RealScalar.of(14145));
    assertEquals(LCM.of(RealScalar.of(+123), RealScalar.of(-345)), RealScalar.of(14145));
    assertEquals(LCM.of(RealScalar.of(-123), RealScalar.of(-345)), RealScalar.of(14145));
  }

  @Test
  void testReduce() {
    Scalar scalar = Tensors.vector(13 * 700, 64 * 7, 4 * 7 * 13).stream() //
        .map(Scalar.class::cast) //
        .reduce(LCM::of).get();
    assertEquals(scalar.toString(), "145600");
  }

  @Test
  void testRational() {
    Scalar scalar = LCM.of(Rational.of(3, 2), Rational.of(2, 1));
    assertEquals(scalar, RealScalar.of(6)); // Mathematica gives 6
  }

  @Test
  void testComplex() {
    Scalar scalar = LCM.of(ComplexScalar.of(2, 1), ComplexScalar.of(3, 1));
    assertEquals(scalar, ComplexScalar.of(5, -5)); // Mathematica gives 5 + 5 I
  }
}

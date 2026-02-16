// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;

class GCDTest {
  @Test
  void testExamples() {
    assertEquals(GCD.of(RealScalar.of(+90), RealScalar.of(+60)), RealScalar.of(30));
    assertEquals(GCD.of(RealScalar.of(+90), RealScalar.of(-60)), RealScalar.of(30));
    assertEquals(GCD.of(RealScalar.of(-90), RealScalar.of(-60)), RealScalar.of(30));
    assertEquals(GCD.of(RealScalar.of(-90), RealScalar.of(+60)), RealScalar.of(30));
  }

  @Test
  void testZero() {
    assertEquals(GCD.of(RealScalar.of(0), RealScalar.of(+60)), RealScalar.of(60));
    assertEquals(GCD.of(RealScalar.of(+60), RealScalar.of(0)), RealScalar.of(60));
    assertEquals(GCD.of(RealScalar.of(0), RealScalar.of(-60)), RealScalar.of(60));
    assertEquals(GCD.of(RealScalar.of(-60), RealScalar.of(0)), RealScalar.of(60));
    assertEquals(GCD.of(RealScalar.of(0), RealScalar.of(0)), RealScalar.of(0));
  }

  @Test
  void testReduce() {
    Scalar scalar = Tensors.vector(13 * 700, 64 * 7, 4 * 7 * 13).stream() //
        .map(Scalar.class::cast) //
        .reduce(GCD::of).get();
    assertEquals(scalar.toString(), "28");
  }

  @Test
  void testRational() {
    Scalar scalar = GCD.of(Rational.of(3, 2), Rational.of(2, 1));
    assertEquals(scalar, Rational.HALF); // Mathematica gives 1/2
  }

  @Test
  void testNumber() {
    assertEquals(GCD.of(2, 15), RealScalar.of(1));
    assertEquals(GCD.of(10, 15), RealScalar.of(5));
    assertEquals(GCD.of(0, 15), RealScalar.of(15));
  }

  @Test
  void testComplex1() {
    Scalar scalar = GCD.of(ComplexScalar.of(2, 1), ComplexScalar.of(3, 1));
    assertEquals(scalar, ComplexScalar.I); // Mathematica gives 1
  }

  @Test
  void testComplex2() {
    // GCD[9 + 3 I, 123 + 9 I]
    Scalar scalar = GCD.of(ComplexScalar.of(9, 3), ComplexScalar.of(123, 9));
    assertEquals(scalar, ComplexScalar.of(-3, 3));
  }

  @Test
  void testQuantity() {
    Scalar scalar = GCD.of(Quantity.of(2 * 7 * 5, "s"), Quantity.of(2 * 5 * 13, "s"));
    assertEquals(scalar, Quantity.of(2 * 5, "s"));
  }

  @Test
  void testNumericFail() {
    assertThrows(Throw.class, () -> GCD.of(RealScalar.of(0.3), RealScalar.of(+60)));
    assertThrows(Throw.class, () -> GCD.of(RealScalar.of(123), RealScalar.of(0.2)));
  }
}

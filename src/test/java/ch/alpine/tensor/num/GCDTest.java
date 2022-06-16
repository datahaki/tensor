// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
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
    Scalar scalar = GCD.of(RationalScalar.of(3, 2), RationalScalar.of(2, 1));
    assertEquals(scalar, RationalScalar.HALF); // Mathematica gives 1/2
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
    assertThrows(TensorRuntimeException.class, () -> GCD.of(RealScalar.of(0.3), RealScalar.of(+60)));
    assertThrows(TensorRuntimeException.class, () -> GCD.of(RealScalar.of(123), RealScalar.of(0.2)));
  }
}

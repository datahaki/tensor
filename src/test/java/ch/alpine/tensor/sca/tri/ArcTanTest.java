// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;

class ArcTanTest {
  @Test
  void testReal() {
    Scalar s = RealScalar.of(-3);
    Scalar r = ArcTan.FUNCTION.apply(s);
    assertEquals(r, ArcTan.of(s));
    // -1.5707963267948966192 + 1.7627471740390860505 I
    assertEquals(r, Scalars.fromString("-1.2490457723982544"));
  }

  @Test
  void testRealZero() {
    assertEquals(ArcTan.of(RealScalar.ZERO, RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(ArcTan.of(0, 0), RealScalar.ZERO);
  }

  @Test
  void testNumber() {
    assertEquals(ArcTan.of(2, 0), RealScalar.ZERO);
    assertEquals(ArcTan.of(-3, 0), RealScalar.of(Math.PI));
    assertEquals(ArcTan.of(0, 4), RealScalar.of(Math.PI / 2));
    assertEquals(ArcTan.of(0, -5), RealScalar.of(-Math.PI / 2));
  }

  @Test
  void testComplexReal() {
    Scalar r = ArcTan.of(ComplexScalar.of(2, 3), RealScalar.of(12));
    // 1.39519 - 0.247768 I
    assertEquals(r, Scalars.fromString("1.3951860877095887-0.24776768676598088*I"));
  }

  @Test
  void testComplex() {
    Scalar s = ComplexScalar.of(5, -7);
    Scalar r = ArcTan.FUNCTION.apply(s);
    assertEquals(r, ArcTan.of(s));
    // 1.50273 - 0.0944406 I
    assertEquals(r, Scalars.fromString("1.502726846368326-0.09444062638970714*I"));
  }

  @Test
  void testComplex2() {
    Scalar x = ComplexScalar.of(4, -1);
    Scalar y = ComplexScalar.of(1, 2);
    Scalar r = ArcTan.of(x, y);
    // 0.160875 + 0.575646 I
    assertEquals(r, Scalars.fromString("0.1608752771983211+0.5756462732485114*I"));
  }

  @Test
  void testComplexZeroP() {
    Scalar x = RealScalar.ZERO;
    Scalar y = ComplexScalar.of(1, 2);
    Scalar r = ArcTan.of(x, y);
    assertEquals(r, DoubleScalar.of(Math.PI / 2));
  }

  @Test
  void testComplexZeroN() {
    Scalar x = RealScalar.ZERO;
    Scalar y = ComplexScalar.of(-1, 2);
    Scalar r = ArcTan.of(x, y);
    assertEquals(r, DoubleScalar.of(-Math.PI / 2));
  }

  @Test
  void testCornerCases() {
    assertEquals(ArcTan.of(RealScalar.of(-5), RealScalar.ZERO), Pi.VALUE);
    assertEquals(ArcTan.of(RealScalar.ZERO, RealScalar.ZERO), RealScalar.ZERO);
  }

  // Mathematica doesn't do this:
  // ArcTan[Quantity[12, "Meters"], Quantity[4, "Meters"]] is not evaluated
  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(12, "m");
    Scalar qs2 = Quantity.of(4, "m");
    {
      assertFalse(qs1 instanceof RealScalar);
      Scalar res = ArcTan.of(qs1, qs2);
      assertInstanceOf(RealScalar.class, res);
      Tolerance.CHOP.requireClose(res, RealScalar.of(0.32175055439664219340));
    }
  }

  @Test
  void testQuantityZeroX() {
    Scalar qs0 = Quantity.of(0, "m");
    Scalar qs1 = Quantity.of(12, "m");
    {
      Scalar res = ArcTan.of(qs0, qs1);
      assertInstanceOf(RealScalar.class, res);
      Tolerance.CHOP.requireClose(res, RealScalar.of(Math.PI / 2));
    }
    {
      Scalar res = ArcTan.of(qs1, qs0);
      assertInstanceOf(RealScalar.class, res);
      Tolerance.CHOP.requireZero(res);
    }
  }

  @Test
  void testAntiSymmetry() {
    Distribution distribution = TriangularDistribution.with(0, 1);
    Distribution scaling = UniformDistribution.of(0.1, 2);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar y = RandomVariate.of(distribution);
      Scalar lambda = RandomVariate.of(scaling);
      Scalar v1 = ArcTan.of(x, y);
      Scalar v2 = ArcTan.of(x, y.negate());
      Scalar v3 = ArcTan.of(x.multiply(lambda), y.multiply(lambda));
      assertEquals(v1, v2.negate());
      Tolerance.CHOP.requireClose(v1, v3);
    }
  }

  @Test
  void testDoubleNaNFail() {
    assertThrows(Throw.class, () -> ArcTan.FUNCTION.apply(ComplexScalar.of(Double.NaN, Double.NaN)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> ArcTan.of(Quantity.of(12, "m"), Quantity.of(4, "s")));
    assertThrows(Throw.class, () -> ArcTan.of(Quantity.of(12, "m"), RealScalar.of(4)));
    assertThrows(Throw.class, () -> ArcTan.of(RealScalar.of(12), Quantity.of(4, "s")));
  }

  @Test
  void testGaussScalarFail() {
    Tensor tensor = Tensors.fromString("{0.3, 1/3, 3+4*I, 1.2+3.4*I}");
    for (Tensor _x : tensor) {
      Scalar x = (Scalar) _x;
      assertThrows(Throw.class, () -> ArcTan.of(x, GaussScalar.of(1, 7)));
      assertThrows(Throw.class, () -> ArcTan.of(GaussScalar.of(1, 7), x));
    }
  }
}

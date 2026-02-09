// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;

class RootsBoundsTest {
  @RepeatedTest(10)
  void testQuadratic() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Tensor coeffs = RandomVariate.of(distribution, 3);
    Tensor roots = Roots.of(coeffs);
    Scalar max = roots.maps(Abs.FUNCTION).stream().map(Scalar.class::cast).reduce(Max::of).orElseThrow();
    for (RootsBounds rootsBounds : RootsBounds.values()) {
      Scalar bound = rootsBounds.of(coeffs);
      boolean check = Scalars.lessEquals(max, bound);
      if (!check) {
        System.err.println(rootsBounds + " " + max + " " + bound + "  " + coeffs);
      }
      assertTrue(check);
    }
  }

  @RepeatedTest(10)
  void testCubic() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Tensor coeffs = RandomVariate.of(distribution, 4);
    Tensor roots = Roots.of(coeffs);
    Scalar max = roots.maps(Abs.FUNCTION).stream().map(Scalar.class::cast).reduce(Max::of).orElseThrow();
    for (RootsBounds rootsBounds : RootsBounds.values()) {
      Scalar bound = rootsBounds.of(coeffs);
      boolean check = Scalars.lessEquals(max, bound);
      if (!check) {
        System.err.println(rootsBounds + " " + max + " " + bound + "  " + coeffs);
      }
      assertTrue(check);
    }
  }

  @Test
  void testQuantity() {
    Tensor coeffs = Tensors.fromString("{3[m], 2[m*s^-1], 3[m*s^-2], -4[m*s^-3]}");
    Roots.bound(coeffs);
    Polynomial polynomial = Polynomial.of(coeffs);
    assertEquals(polynomial.getZeroDomain(), Quantity.of(0, "s"));
    assertEquals(polynomial.getUnitValues(), Unit.of("m"));
    polynomial.apply(Quantity.of(4, "s"));
    for (RootsBounds rootsBounds : RootsBounds.values())
      try {
        rootsBounds.of(coeffs);
      } catch (Exception exception) {
        exception.getMessage();
      }
  }

  @Test
  void testQuantitySimple() {
    Tensor coeffs = Tensors.fromString("{3, 2[s^-1], 3[s^-2], -4[s^-3]}");
    Roots.bound(coeffs);
    Polynomial polynomial = Polynomial.of(coeffs);
    assertEquals(polynomial.getZeroDomain(), Quantity.of(0, "s"));
    assertEquals(polynomial.getUnitValues(), Unit.of(""));
    polynomial.apply(Quantity.of(4, "s"));
    for (RootsBounds rootsBounds : RootsBounds.values())
      try {
        rootsBounds.of(coeffs);
      } catch (Exception exception) {
        exception.getMessage();
      }
  }

  @Test
  void testQuantity3() {
    Tensor coeffs = Tensors.fromString("{3[m], 2[m], 3[m], -4[m]}");
    Roots.bound(coeffs);
    Polynomial polynomial = Polynomial.of(coeffs);
    assertEquals(polynomial.getZeroDomain(), RealScalar.ZERO);
    assertEquals(polynomial.getUnitValues(), Unit.of("m"));
    polynomial.apply(RealScalar.of(4));
    for (RootsBounds rootsBounds : RootsBounds.values())
      rootsBounds.of(coeffs);
  }

  @Test
  void testSpecific() {
    Tensor coeffs = Tensors.fromString("{-3, 2[m^-1], 3[m^-2], -2[m^-3]}");
    Polynomial polynomial = Polynomial.of(coeffs);
    assertEquals(polynomial.apply(Quantity.of(2, "m")), RealScalar.of(-3));
    Scalar scalar = RootsBounds.FUJIWARA.of(coeffs);
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(3, "m"));
    Tensor tensor = Roots.of(coeffs);
    assertEquals(tensor, Tensors.fromString("{-1[m], 1[m], 3/2[m]}"));
  }
}

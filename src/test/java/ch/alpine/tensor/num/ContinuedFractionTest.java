// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.ply.Polynomial;

class ContinuedFractionTest {
  @Test
  void testPi() {
    double x = Math.PI;
    Tensor cf = ContinuedFraction.FLOOR.of(RealScalar.of(x), 20, Tolerance.CHOP);
    Tensor expect = Tensors.vector(3, 7, 15, 1, 292, 1, 1, 1, 2, 1, 3, 1, 14, 3, 3, 23, 1, 1, 7, 4);
    assertEquals(cf, expect);
    Tensor approx = Tensors.vector(3, 7, 15, 1);
    Scalar ratio = FromContinuedFraction.of(approx);
    assertEquals(ratio.toString(), "355/113");
  }

  @Test
  void testPiRound() {
    double x = Math.PI;
    Tensor cf = ContinuedFraction.ROUND.of(RealScalar.of(x), 20, Tolerance.CHOP);
    Tensor expect = Tensors.vector(3, 7, 16, -294, 3, -4, 5, -15, -3, -3, -24, 3, 4, -3, -8, -2, -4, 2, 7, -6);
    assertEquals(cf, expect);
    Tensor approx = Tensors.vector(3, 7, 16);
    Scalar ratio = FromContinuedFraction.of(approx);
    assertEquals(ratio.toString(), "355/113");
  }

  @ParameterizedTest
  @EnumSource
  void testPiNeg(ContinuedFraction continuedFraction) {
    Scalar x = RealScalar.of(-Math.PI);
    Tensor cf = continuedFraction.of(x, 20, Tolerance.CHOP);
    Scalar result = FromContinuedFraction.of(cf);
    assertEquals(result, x);
  }

  @Test
  void testRational() {
    Tensor cf = ContinuedFraction.FLOOR.of(Rational.THIRD, 20, Tolerance.CHOP);
    assertEquals(cf, Tensors.vector(0, 3));
  }

  @Test
  void testRational2() {
    Scalar x = Rational.of(2342343453123L, 12312434);
    Tensor cf = ContinuedFraction.FLOOR.of(x, 200, Chop.NONE);
    Tensor expect = Tensors.vector(190242, 8, 1, 8, 1, 1, 2, 2, 16, 5, 2, 4, 1, 1, 1, 1, 2);
    assertEquals(cf, expect);
    assertEquals(x, FromContinuedFraction.of(expect));
  }

  @Test
  void testRational2Q() {
    Scalar x = Quantity.of(Rational.of(2342343453123L, 12312434), "m*s^-1");
    Tensor cf = ContinuedFraction.FLOOR.of(x, 200, Chop.NONE);
    assertEquals(x, FromContinuedFraction.of(cf));
  }

  @Test
  void testRational2QNeg() {
    Scalar x = Quantity.of(Rational.of(-2342343453123L, 12312434), "m*s^-1");
    Tensor cf = ContinuedFraction.FLOOR.of(x, 200, Chop.NONE);
    assertEquals(x, FromContinuedFraction.of(cf));
  }

  @ParameterizedTest
  @EnumSource
  void testQuantity(ContinuedFraction continuedFraction) {
    Scalar x = Quantity.of(2.34, "m");
    Tensor cf = continuedFraction.of(x, 20, Tolerance.CHOP);
    Scalar result = FromContinuedFraction.of(cf);
    assertEquals(result, x);
  }

  @Test
  void testOneArg() {
    Scalar x = RealScalar.of(1.000000000001);
    Tensor cf = ContinuedFraction.FLOOR.of(x, 20, Tolerance.CHOP);
    Scalar result = FromContinuedFraction.of(cf);
    Scalar y = N.DOUBLE.apply(result);
    assertEquals(x, y);
  }

  @Test
  void testCubic() {
    Polynomial p1 = Polynomial.fromRoots(Tensors.fromString("{7/99}"));
    Polynomial p2 = Polynomial.of(Tensors.vector(3, 0, 3));
    Polynomial p3 = p1.times(p2);
    Tensor roots = p3.roots();
    Tensor tensor = ContinuedFraction.ROUND.of(roots.Get(2), 20, Tolerance.CHOP);
    // IO.println(tensor);
    Scalar guess = FromContinuedFraction.of(tensor.extract(0, 3));
    assertEquals(guess.toString(), "7/99");
    assertEquals(p3.apply(guess), RealScalar.ZERO);
  }
}

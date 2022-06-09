// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.tri.ArcTan;

class AbstractRealScalarTest {
  private static final List<Scalar> SCALARS = Arrays.asList( //
      RealScalar.ZERO, RealScalar.ONE, //
      RationalScalar.of(-7, 3), //
      RealScalar.of(-3.4), //
      DecimalScalar.of(new BigDecimal("4.123"), 34), //
      Pi.in(50), //
      ComplexScalar.of(2, 3), //
      ComplexScalar.of(6, -8.3), //
      ComplexScalar.of(RealScalar.of(2.3), RationalScalar.of(2, 9)), //
      ComplexScalar.of(Pi.in(40), RationalScalar.of(-4, 5)), //
      ComplexScalar.of(RationalScalar.of(-2, 17), Pi.in(20)), //
      ComplexScalar.of(RationalScalar.of(-7, 13), RationalScalar.of(53, 887)));

  private static void _checkAdd(Scalar a, Scalar b) {
    {
      Scalar ab = a.add(b);
      Scalar ba = b.add(a);
      assertEquals(ab, ba);
      assertEquals(ab.toString(), ba.toString());
      assertEquals(ab.getClass(), ba.getClass());
    }
    {
      Scalar ab = a.subtract(b);
      Scalar ba = b.subtract(a).negate();
      assertEquals(ab, ba);
      if (Scalars.nonZero(ab))
        assertEquals(ab.toString(), ba.toString());
      assertEquals(ab.getClass(), ba.getClass());
    }
  }

  @Test
  public void testAdd() {
    Unit unit = Unit.of("m^2*s^-3");
    List<Scalar> list = SCALARS;
    for (int i = 0; i < list.size(); ++i)
      for (int j = i; j < list.size(); ++j) {
        Scalar a = list.get(i);
        Scalar b = list.get(j);
        _checkAdd(a, b);
        _checkAdd(Quantity.of(a, unit), Quantity.of(b, unit));
      }
  }

  private static void _checkMultiply(Scalar a, Scalar b) {
    Scalar ab = a.multiply(b);
    Scalar ba = b.multiply(a);
    assertEquals(ab, ba);
    assertEquals(ab.toString(), ba.toString());
    assertEquals(ab.getClass(), ba.getClass());
  }

  @Test
  public void testMultiply() {
    Unit ua = Unit.of("m^2*s^-3");
    Unit ub = Unit.of("kg*CHF^-1");
    List<Scalar> list = SCALARS;
    for (int i = 0; i < list.size(); ++i)
      for (int j = i; j < list.size(); ++j) {
        Scalar a = list.get(i);
        Scalar b = list.get(j);
        _checkMultiply(a, b);
        _checkMultiply(Quantity.of(a, ua), Quantity.of(b, ua));
        _checkMultiply(Quantity.of(a, ua), Quantity.of(b, ub));
        _checkMultiply(Quantity.of(a, ub), Quantity.of(b, ua));
        _checkMultiply(Quantity.of(a, ub), Quantity.of(b, ub));
      }
  }

  @Test
  public void testPermutationZero() {
    Tensor vector = Tensors.of(RealScalar.ZERO, Quantity.of(0, "m"), Quantity.of(0, "s"));
    Tensor produc = Tensor.of(Permutations.stream(Range.of(0, vector.length())) //
        .map(Primitives::toIntArray) //
        .map(array -> IntStream.of(array).mapToObj(vector::Get).reduce(Scalar::multiply).get()));
    assertEquals(produc, ConstantArray.of(Quantity.of(0, "m*s"), 6));
  }

  private static void _checkDivide(Scalar a, Scalar b) {
    if (Scalars.nonZero(b)) {
      Scalar ab = a.divide(b);
      Scalar ba = b.under(a);
      assertEquals(ab, ba);
      assertEquals(ab.toString(), ba.toString());
      assertEquals(ab.getClass(), ba.getClass());
    }
    if (Scalars.nonZero(a) && Scalars.nonZero(b)) {
      Scalar ab = a.divide(b);
      Scalar ba = a.under(b).reciprocal();
      Chop._13.requireClose(ab, ba);
      assertEquals(ab.getClass(), ba.getClass());
    }
  }

  @Test
  public void testDivide() {
    Unit ua = Unit.of("m^2*s^-3");
    Unit ub = Unit.of("kg*CHF^-1");
    List<Scalar> list = SCALARS;
    for (int i = 0; i < list.size(); ++i)
      for (int j = i; j < list.size(); ++j) {
        Scalar a = list.get(i);
        Scalar b = list.get(j);
        _checkDivide(a, b);
        _checkDivide(Quantity.of(a, ua), Quantity.of(b, ua));
        _checkDivide(Quantity.of(a, ua), Quantity.of(b, ub));
        _checkDivide(Quantity.of(a, ub), Quantity.of(b, ua));
        _checkDivide(Quantity.of(a, ub), Quantity.of(b, ub));
      }
  }

  @Test
  public void testArcTan() {
    assertThrows(TensorRuntimeException.class, () -> ArcTan.of(RealScalar.of(2.3), GaussScalar.of(3, 7)));
    assertThrows(TensorRuntimeException.class, () -> ArcTan.of(GaussScalar.of(3, 7), RealScalar.of(2.3)));
  }

  @Test
  public void testRange() {
    assertEquals(Math.log(AbstractRealScalar.LOG_HI), Math.log1p(AbstractRealScalar.LOG_HI - 1));
    assertEquals(Math.log(AbstractRealScalar.LOG_LO), Math.log1p(AbstractRealScalar.LOG_LO - 1));
  }

  @Test
  public void testPower00() {
    Scalar one = Power.of(0, 0);
    ExactScalarQ.require(one);
    assertEquals(one, RealScalar.ONE);
  }

  @Test
  public void testPower00Numeric() {
    Scalar one = Power.of(0.0, 0.0);
    ExactScalarQ.require(one);
    assertEquals(one, RealScalar.ONE);
  }

  @Test
  public void testPowerFail() {
    assertThrows(TensorRuntimeException.class, () -> Power.of(1, GaussScalar.of(2, 7)));
  }
}

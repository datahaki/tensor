// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;

class NTest {
  @Test
  public void testZero() {
    Scalar result = N.DOUBLE.of(RealScalar.ZERO);
    assertInstanceOf(DoubleScalar.class, result);
    assertEquals(result.toString(), "0.0");
  }

  @Test
  public void testReal() {
    Scalar scalar = RationalScalar.of(3, 5);
    assertEquals(scalar.toString(), "3/5");
    assertEquals(N.DOUBLE.of(scalar).toString(), "" + (3 / 5.0));
  }

  @Test
  public void testComplex() {
    Scalar scalar = ComplexScalar.of(3, 5);
    assertEquals(scalar.toString(), "3+5*I");
    assertEquals(N.DOUBLE.of(scalar).toString(), "3.0+5.0*I");
  }

  @Test
  public void testDoubleId() {
    assertInstanceOf(DoubleScalar.class, N.DOUBLE.of(RealScalar.of(3.14)));
    assertInstanceOf(DoubleScalar.class, N.DECIMAL32.of(RealScalar.of(3.14)));
  }

  @Test
  public void testContext() {
    Scalar d = N.DECIMAL128.of(RealScalar.of(3).reciprocal());
    assertEquals(d.toString(), "0.3333333333333333333333333333333333`34");
    Scalar d32 = N.DECIMAL32.apply(d);
    assertEquals(d32.toString(), "0.3333333");
  }

  @Test
  public void testComplexContext() {
    Scalar scalar = ComplexScalar.of(3, 7).reciprocal();
    ExactScalarQ.require(scalar);
    Scalar d = N.DECIMAL128.of(scalar);
    // mathematica gives ...... 0.05172413793103448275862068965517241...-0.12068965517241379310344827586206897 I
    assertEquals(d.toString(), "0.05172413793103448275862068965517241`34-0.1206896551724137931034482758620690`34*I");
  }

  @Test
  public void testQuantity() {
    Quantity n = (Quantity) N.DECIMAL128.apply(Quantity.of(RationalScalar.of(1, 7), "cd^-2*K"));
    assertInstanceOf(DecimalScalar.class, n.value());
    assertEquals(Unit.of("  K * cd  ^  -2"), n.unit());
  }
}

// code by jph
package ch.alpine.tensor.sca.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class ExpTest {
  @Test
  void testEuler() {
    Scalar emi = Exp.FUNCTION.apply(ComplexScalar.of(RealScalar.ZERO, Pi.VALUE.negate()));
    Scalar tru = RealScalar.ONE.negate();
    Chop._15.requireClose(emi, tru);
  }

  @Test
  void testExpZero() {
    assertEquals(Exp.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
    assertEquals(Log.FUNCTION.apply(RealScalar.ONE), RealScalar.ZERO);
  }

  @Test
  void testDecimal() {
    Scalar scalar = Exp.FUNCTION.apply(DecimalScalar.of(new BigDecimal("1")));
    assertInstanceOf(DecimalScalar.class, scalar);
    assertTrue(scalar.toString().startsWith("2.71828182845904523536028747135266"));
  }

  @Test
  void testComplexDecimal() {
    Scalar scalar = Exp.FUNCTION.apply(ComplexScalar.of( //
        DecimalScalar.of(new BigDecimal("1")), //
        DecimalScalar.of(new BigDecimal("2.12"))));
    assertInstanceOf(ComplexScalar.class, scalar);
    // mathematica gives -1.4189653368301074` + 2.3185326117622904` I
    Scalar m = Scalars.fromString("-1.4189653368301074 + 2.3185326117622904 * I");
    Chop._15.requireClose(scalar, m);
  }

  @Test
  void testInfty() {
    assertEquals(Exp.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(Exp.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), RealScalar.ZERO);
  }

  @Test
  void testEmpty() {
    assertEquals(Tensors.empty().map(Exp.FUNCTION), Tensors.empty());
  }

  @Test
  void testFailQuantity() {
    Scalar scalar = Quantity.of(2, "m");
    assertThrows(Throw.class, () -> Exp.FUNCTION.apply(scalar));
  }

  @Test
  void testFail() {
    Scalar scalar = GaussScalar.of(6, 7);
    assertThrows(Throw.class, () -> Exp.FUNCTION.apply(scalar));
  }
}

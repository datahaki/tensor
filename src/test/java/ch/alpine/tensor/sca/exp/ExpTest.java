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
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class ExpTest {
  @Test
  public void testEuler() {
    Scalar emi = Exp.of(ComplexScalar.of(RealScalar.ZERO, Pi.VALUE.negate()));
    Scalar tru = RealScalar.ONE.negate();
    Chop._15.requireClose(emi, tru);
  }

  @Test
  public void testExpZero() {
    assertEquals(Exp.of(RealScalar.ZERO), RealScalar.ONE);
    assertEquals(Log.of(RealScalar.ONE), RealScalar.ZERO);
  }

  @Test
  public void testDecimal() {
    Scalar scalar = Exp.of(DecimalScalar.of(new BigDecimal("1")));
    assertInstanceOf(DecimalScalar.class, scalar);
    assertTrue(scalar.toString().startsWith("2.71828182845904523536028747135266"));
  }

  @Test
  public void testComplexDecimal() {
    Scalar scalar = Exp.of(ComplexScalar.of( //
        DecimalScalar.of(new BigDecimal("1")), //
        DecimalScalar.of(new BigDecimal("2.12"))));
    assertInstanceOf(ComplexScalar.class, scalar);
    // mathematica gives -1.4189653368301074` + 2.3185326117622904` I
    Scalar m = Scalars.fromString("-1.4189653368301074 + 2.3185326117622904 * I");
    Chop._15.requireClose(scalar, m);
  }

  @Test
  public void testEmpty() {
    assertEquals(Exp.of(Tensors.empty()), Tensors.empty());
  }

  @Test
  public void testFailQuantity() {
    Scalar scalar = Quantity.of(2, "m");
    assertThrows(TensorRuntimeException.class, () -> Exp.of(scalar));
  }

  @Test
  public void testFail() {
    Scalar scalar = GaussScalar.of(6, 7);
    assertThrows(TensorRuntimeException.class, () -> Exp.of(scalar));
  }
}

// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ExpTest extends TestCase {
  public void testEuler() {
    Scalar emi = Exp.of(ComplexScalar.of(RealScalar.ZERO, Pi.VALUE.negate()));
    Scalar tru = RealScalar.ONE.negate();
    Chop._15.requireClose(emi, tru);
  }

  public void testExpZero() {
    assertEquals(Exp.of(RealScalar.ZERO), RealScalar.ONE);
    assertEquals(Log.of(RealScalar.ONE), RealScalar.ZERO);
  }

  public void testDecimal() {
    Scalar scalar = Exp.of(DecimalScalar.of(new BigDecimal("1")));
    assertTrue(scalar instanceof DecimalScalar);
    assertTrue(scalar.toString().startsWith("2.71828182845904523536028747135266"));
  }

  public void testComplexDecimal() {
    Scalar scalar = Exp.of(ComplexScalar.of( //
        DecimalScalar.of(new BigDecimal("1")), //
        DecimalScalar.of(new BigDecimal("2.12"))));
    assertTrue(scalar instanceof ComplexScalar);
    // mathematica gives -1.4189653368301074` + 2.3185326117622904` I
    Scalar m = Scalars.fromString("-1.4189653368301074 + 2.3185326117622904 * I");
    Chop._15.requireClose(scalar, m);
  }

  public void testEmpty() {
    assertEquals(Exp.of(Tensors.empty()), Tensors.empty());
  }

  public void testFailQuantity() {
    Scalar scalar = Quantity.of(2, "m");
    AssertFail.of(() -> Exp.of(scalar));
  }

  public void testFail() {
    Scalar scalar = GaussScalar.of(6, 7);
    AssertFail.of(() -> Exp.of(scalar));
  }
}

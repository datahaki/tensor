// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ArgTest extends TestCase {
  public void testArg() {
    Scalar scalar = ComplexScalar.of(RationalScalar.of(-2, 3), RationalScalar.of(-5, 100));
    assertEquals(Arg.of(scalar).toString(), "-3.066732805879026");
    assertEquals(Arg.of(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Arg.of(DoubleScalar.of(0.2)), RealScalar.ZERO);
    assertEquals(Arg.of(DoubleScalar.of(-1)), DoubleScalar.of(Math.PI));
    assertEquals(Arg.of(RationalScalar.of(-1, 3)), DoubleScalar.of(Math.PI));
  }

  public void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    assertEquals(Arg.of(DecimalScalar.of(new BigDecimal("3.14", mc), mc.getPrecision())), RealScalar.ZERO);
    assertEquals(Arg.of(DecimalScalar.of(new BigDecimal("-112.14", mc), mc.getPrecision())), RealScalar.of(Math.PI));
  }

  public void testQuantity() {
    Unit unit = Unit.of("s*m^3");
    Scalar s = Quantity.of(ComplexScalar.of(3, 4), unit);
    Scalar a = Arg.of(s);
    Scalar b = ArcTan.of(RealScalar.of(3), RealScalar.of(4));
    assertEquals(a, b);
  }

  public void testQuaternionFail() {
    AssertFail.of(() -> Arg.FUNCTION.apply(Quaternion.of(1, 2, 3, 4)));
  }

  public void testGaussScalarFail() {
    Scalar scalar = GaussScalar.of(1, 7);
    AssertFail.of(() -> Arg.of(scalar));
  }
}

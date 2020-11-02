// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuantityUnitTest extends TestCase {
  public void testParsecs() {
    // QuantityMagnitude[Quantity[1*^-12, "Parsecs"], "Kilometers"]
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("km");
    Scalar scalar = scalarUnaryOperator.apply(Quantity.of(1e-12, "pc"));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(30.85677581467192));
  }

  public void testQuantity() {
    assertEquals(QuantityUnit.of(Quantity.of(2, "m*V")), Unit.of("m*V"));
    assertEquals(QuantityUnit.of(Quantity.of(3, "kg*V^-2")), Unit.of("V^-1*kg*V^-1"));
    assertEquals(QuantityUnit.of(Quantity.of(4, "tpf")), Unit.of("tpf"));
  }

  public void testScalar() {
    assertEquals(QuantityUnit.of(RealScalar.ONE), Unit.ONE);
    assertEquals(QuantityUnit.of(GaussScalar.of(2, 5)), Unit.ONE);
    assertEquals(QuantityUnit.of(StringScalar.of("abc[s]")), Unit.ONE);
  }

  public void testPercent() {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(1000000, "%^3"));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testNullFail() {
    AssertFail.of(() -> QuantityUnit.of(null));
  }
}

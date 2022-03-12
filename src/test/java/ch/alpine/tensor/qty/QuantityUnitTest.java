// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
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

  public void testBytes() {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(10, "KiB"));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(10240, "B"));
  }

  public void testClips() {
    assertEquals(QuantityUnit.of(Clips.positive(3)), Unit.ONE);
    assertEquals(QuantityUnit.of(Clips.absolute(Quantity.of(3, "m^3"))), Unit.of("m^3"));
  }

  public void testNullFail() {
    AssertFail.of(() -> QuantityUnit.of((Scalar) null));
    AssertFail.of(() -> QuantityUnit.of((Clip) null));
  }
}

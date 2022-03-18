// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

public class QuantityMagnitudeTest {
  @Test
  public void testMagnitudeKgf() {
    Scalar scalar = QuantityMagnitude.SI().in("N").apply(Quantity.of(1, "kgf"));
    assertEquals(N.DOUBLE.apply(scalar).number().doubleValue(), 9.80665);
  }

  @Test
  public void testInUnit() {
    Scalar scalar = QuantityMagnitude.SI().in(Unit.of("K*m^2")).apply(Quantity.of(2, "K*km^2"));
    assertEquals(scalar, RealScalar.of(2_000_000));
  }

  @Test
  public void testInString() {
    Scalar scalar = QuantityMagnitude.SI().in("K*m^2*s").apply(Quantity.of(2, "K*km^2*s"));
    assertEquals(scalar, RealScalar.of(2_000_000));
  }

  @Test
  public void testRad() throws ClassNotFoundException, IOException {
    QuantityMagnitude quantityMagnitude = Serialization.copy(QuantityMagnitude.SI());
    ScalarUnaryOperator scalarUnaryOperator = quantityMagnitude.in(Unit.of("rad"));
    Chop._12.requireClose(scalarUnaryOperator.apply(Quantity.of(360, "deg")), RealScalar.of(Math.PI * 2));
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(2));
    assertEquals(scalar, RealScalar.of(2));
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testSingleton() {
    Scalar scalar = Quantity.of(3, "m^2*s");
    ScalarUnaryOperator suo = QuantityMagnitude.singleton("s*m^2");
    assertEquals(suo.apply(scalar), RealScalar.of(3));
  }

  @Test
  public void testSingleton2() {
    Scalar scalar = RealScalar.of(3);
    ScalarUnaryOperator suo = QuantityMagnitude.singleton(Unit.ONE);
    assertEquals(suo.apply(scalar), RealScalar.of(3));
  }

  @Test
  public void testConversionJ() {
    Scalar scalar = Quantity.of(6.241509125883258E9, "GeV");
    QuantityMagnitude quantityMagnitude = QuantityMagnitude.SI();
    ScalarUnaryOperator suo = quantityMagnitude.in("J");
    Scalar result = suo.apply(scalar);
    Tolerance.CHOP.requireClose(result, RealScalar.ONE);
  }

  @Test
  public void testConversionPa() {
    Scalar scalar = Quantity.of(RationalScalar.of(8896443230521L, 1290320000).reciprocal(), "psi");
    QuantityMagnitude quantityMagnitude = QuantityMagnitude.SI();
    ScalarUnaryOperator suo = quantityMagnitude.in("Pa");
    Scalar result = suo.apply(scalar);
    assertEquals(result, RealScalar.ONE);
    ExactScalarQ.require(result);
  }

  @Test
  public void testConversionN() {
    Scalar scalar = Quantity.of(RationalScalar.of(8896443230521L, 2000000000000L).reciprocal(), "lbf");
    QuantityMagnitude quantityMagnitude = QuantityMagnitude.SI();
    ScalarUnaryOperator scalarUnaryOperator = quantityMagnitude.in("N");
    Scalar result = scalarUnaryOperator.apply(scalar);
    assertEquals(result, RealScalar.ONE);
    ExactScalarQ.require(result);
  }

  @Test
  public void testConversionMoWk() {
    Scalar scalar = Quantity.of(1, "mo");
    QuantityMagnitude quantityMagnitude = QuantityMagnitude.SI();
    ScalarUnaryOperator scalarUnaryOperator = quantityMagnitude.in("wk");
    Scalar result = scalarUnaryOperator.apply(scalar);
    assertEquals(result, RationalScalar.of(365, 84));
  }

  @Test
  public void testHorsepower() {
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("W");
    Scalar ps = scalarUnaryOperator.apply(Quantity.of(1.0, "PS"));
    Scalar hp = scalarUnaryOperator.apply(Quantity.of(1.0, "hp"));
    assertEquals(ps, RealScalar.of(735.49875));
    Chop._12.requireClose(hp, RealScalar.of(745.6998715822702));
  }

  @Test
  public void testHorsepowerKiloWatts() {
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("kW");
    Scalar ps = scalarUnaryOperator.apply(Quantity.of(1.0, "PS"));
    Scalar hp = scalarUnaryOperator.apply(Quantity.of(1.0, "hp"));
    Chop._14.requireClose(ps, RealScalar.of(0.73549875));
    Chop._14.requireClose(hp, RealScalar.of(0.7456998715822702));
  }

  @Test
  public void testKiloponds() {
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("N");
    Scalar kp = scalarUnaryOperator.apply(Quantity.of(1.0, "kp"));
    assertEquals(kp, RealScalar.of(9.80665));
  }

  @Test
  public void testMetricTons() {
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("t");
    Scalar _1kg_in_tons = scalarUnaryOperator.apply(Quantity.of(1000, "g"));
    ExactScalarQ.require(_1kg_in_tons);
    assertEquals(_1kg_in_tons, RationalScalar.of(1, 1000));
  }

  @Test
  public void testPercent() {
    ScalarUnaryOperator scalarUnaryOperator = QuantityMagnitude.SI().in("%");
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(2));
    assertEquals(scalar, RealScalar.of(200));
    String string = scalarUnaryOperator.toString();
    assertTrue(string.startsWith("QuantityMagnitude"));
    assertTrue(string.contains("%"));
  }

  @Test
  public void testVolume() {
    Tolerance.CHOP.requireClose( //
        QuantityMagnitude.SI().in("L").apply(Quantity.of(1.0, "cup")), //
        DoubleScalar.of(0.2365882365));
    Tolerance.CHOP.requireClose( //
        QuantityMagnitude.SI().in("L").apply(Quantity.of(1.0, "gal")), //
        DoubleScalar.of(3.785411784));
    Tolerance.CHOP.requireClose( //
        QuantityMagnitude.SI().in("L").apply(Quantity.of(1.0, "tsp")), //
        DoubleScalar.of(0.00492892159375));
    // Tolerance.CHOP.requireClose( //
    // QuantityMagnitude.SI().in("L").apply(Quantity.of(1.0, "sticks")), //
    // DoubleScalar.of(0.11829411825));
  }

  @Test
  public void testFailConversion() {
    QuantityMagnitude quantityMagnitude = QuantityMagnitude.SI();
    Scalar quantity = Quantity.of(360, "kg");
    ScalarUnaryOperator scalarUnaryOperator = quantityMagnitude.in("m");
    assertThrows(TensorRuntimeException.class, () -> scalarUnaryOperator.apply(quantity));
  }

  @Test
  public void testFailInNull() {
    assertThrows(NullPointerException.class, () -> QuantityMagnitude.SI().in((Unit) null));
  }

  @Test
  public void testFailNull() {
    assertThrows(NullPointerException.class, () -> new QuantityMagnitude(null));
  }
}

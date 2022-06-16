// code by jph
package ch.alpine.tensor.sca.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class LogTest {
  @Test
  void testOne() {
    Scalar scalar = Log.of(RealScalar.ONE);
    assertTrue(Scalars.isZero(scalar));
  }

  @Test
  void testLog() {
    Scalar scalar = DoubleScalar.of(-3);
    Chop._14.requireClose(Log.of(scalar), Scalars.fromString("1.0986122886681098+3.141592653589793*I"));
    assertEquals(Log.of(RealScalar.ZERO), DoubleScalar.NEGATIVE_INFINITY);
  }

  @Test
  void testComplex() {
    Scalar s = ComplexScalar.of(2, 3);
    Scalar r = Scalars.fromString("1.2824746787307681+0.982793723247329*I"); // mathematica
    Chop._14.requireClose(Log.of(s), r);
  }

  @Test
  void testRational() {
    Scalar rem = Scalars.fromString("1/10000000000");
    Scalar ratio = RealScalar.ONE.add(rem);
    assertEquals(Log.of(ratio).toString(), "" + Math.log1p(rem.number().doubleValue()));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = Log.base(RealScalar.of(2));
    ScalarUnaryOperator copy = Serialization.copy(scalarUnaryOperator);
    Scalar scalar = copy.apply(RealScalar.of(4));
    assertEquals(scalar, RealScalar.of(2));
  }

  @Test
  void testBaseNumber() {
    ScalarUnaryOperator scalarUnaryOperator = Log.base(2);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(64));
    assertEquals(scalar, RealScalar.of(6));
  }

  @Test
  void testStrangeBase() {
    ScalarUnaryOperator scalarUnaryOperator = Log.base(0.1);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(3));
    // Mathematica ............................ -0.47712125471966243730
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-0.47712125471966255));
  }

  @Test
  void testNegativeBase() {
    ScalarUnaryOperator scalarUnaryOperator = Log.base(-0.1);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(3));
    // Mathematica ................. -0.1667368328837891, -0.22749179210112070I
    Scalar result = ComplexScalar.of(-0.1667368328837892, -0.22749179210112075);
    Tolerance.CHOP.requireClose(scalar, result);
  }

  @Test
  void testBaseZero() {
    ScalarUnaryOperator scalarUnaryOperator = Log.base(0);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(+4)), RealScalar.ZERO);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(-4)), RealScalar.ZERO);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(0));
    assertTrue(Double.isNaN(scalar.number().doubleValue()));
    assertFalse(FiniteScalarQ.of(scalar));
  }

  @Test
  void testNaN() {
    assertEquals(Log.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  @Test
  void testBaseOneFail() {
    assertThrows(TensorRuntimeException.class, () -> Log.base(1));
  }

  @Test
  void testFailQuantity() {
    Scalar scalar = Quantity.of(2, "m");
    assertThrows(TensorRuntimeException.class, () -> Log.of(scalar));
  }

  @Test
  void testFail() {
    Scalar scalar = GaussScalar.of(6, 7);
    assertThrows(TensorRuntimeException.class, () -> Log.of(scalar));
  }
}

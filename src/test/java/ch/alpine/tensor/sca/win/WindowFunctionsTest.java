// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class WindowFunctionsTest {
  @ParameterizedTest
  @EnumSource
  void testSimple(WindowFunctions windowFunctions) {
    ScalarUnaryOperator suo = windowFunctions.get();
    assertEquals(suo.apply(RealScalar.of(-0.500001)), RealScalar.ZERO);
    assertEquals(suo.apply(RealScalar.of(+0.500001)), RealScalar.ZERO);
    Chop._15.requireClose(suo.apply(RealScalar.ZERO), RealScalar.ONE);
    assertTrue(suo.toString().startsWith(suo.getClass().getSimpleName()));
  }

  @ParameterizedTest
  @EnumSource
  void testSerializable(WindowFunctions windowFunctions) {
    assertDoesNotThrow(() -> Serialization.copy(windowFunctions.get()));
  }

  @ParameterizedTest
  @EnumSource
  void testSymmetry(WindowFunctions windowFunctions) {
    Distribution distribution = UniformDistribution.of(-0.6, 0.6);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Chop._15.requireClose(windowFunctions.get().apply(x), windowFunctions.get().apply(x.negate()));
    }
  }

  @ParameterizedTest
  @EnumSource
  void testDecimal(WindowFunctions windowFunctions) {
    Distribution distribution = UniformDistribution.unit(30);
    Scalar x = RandomVariate.of(distribution).subtract(Rational.HALF);
    assertInstanceOf(DecimalScalar.class, x);
    Scalar scalar = windowFunctions.get().apply(x);
    // if (scalar instanceof DoubleScalar) {
    // IO.println(windowFunctions);
    // IO.println(x);
    // IO.println(scalar);
    // }
    assertInstanceOf(RealScalar.class, scalar);
  }

  @ParameterizedTest
  @EnumSource
  void testInsideFail(WindowFunctions windowFunctions) {
    assertThrows(Throw.class, () -> windowFunctions.get().apply(Quantity.of(0.1, "s")));
  }

  @ParameterizedTest
  @EnumSource
  void testOustideFail(WindowFunctions windowFunctions) {
    assertThrows(Throw.class, () -> windowFunctions.get().apply(Quantity.of(1, "s")));
  }

  @ParameterizedTest
  @EnumSource
  void testComplexFail(WindowFunctions windowFunctions) {
    Scalar x = ComplexScalar.of(0.1, 0.2);
    assertThrows(Exception.class, () -> windowFunctions.get().apply(x));
  }
}

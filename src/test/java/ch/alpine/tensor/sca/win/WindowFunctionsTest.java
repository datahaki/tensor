// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
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
  @EnumSource(WindowFunctions.class)
  void testSimple(WindowFunctions windowFunction) {
    ScalarUnaryOperator suo = windowFunction.get();
    assertEquals(suo.apply(RealScalar.of(-0.500001)), RealScalar.ZERO);
    assertEquals(suo.apply(RealScalar.of(+0.500001)), RealScalar.ZERO);
    Chop._15.requireClose(suo.apply(RealScalar.ZERO), RealScalar.ONE);
    assertTrue(suo.toString().startsWith(suo.getClass().getSimpleName()));
  }

  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  void testSerializable(WindowFunctions windowFunction) throws ClassNotFoundException, IOException {
    Serialization.copy(windowFunction.get());
  }

  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  void testSymmetry(WindowFunctions windowFunction) {
    Distribution distribution = UniformDistribution.of(-0.6, 0.6);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Chop._15.requireClose(windowFunction.get().apply(x), windowFunction.get().apply(x.negate()));
    }
  }

  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  void testInsideFail(WindowFunctions windowFunction) {
    assertThrows(Throw.class, () -> windowFunction.get().apply(Quantity.of(0.1, "s")));
  }

  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  void testOustideFail(WindowFunctions windowFunction) {
    assertThrows(Throw.class, () -> windowFunction.get().apply(Quantity.of(1, "s")));
  }

  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  void testComplexFail(WindowFunctions windowFunction) {
    Scalar x = ComplexScalar.of(0.1, 0.2);
    assertThrows(Exception.class, () -> windowFunction.get().apply(x));
  }
}

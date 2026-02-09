// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

class StaticHelperTest {
  @ParameterizedTest
  @EnumSource
  void testSimple(WindowFunctions windowFunctions) {
    int[] lengths = new int[] { 1, 2, 3, 4, 10, 32 };
    for (int windowLength : lengths) {
      Tensor weights = SlidingWindow.weights(windowLength, windowFunctions.get());
      Tolerance.CHOP.requireClose(Total.of(weights), RealScalar.of(windowLength));
    }
  }

  @Test
  void testDirichlet() {
    Tensor weights = SlidingWindow.weights(13, DirichletWindow.FUNCTION);
    Tolerance.CHOP.requireClose(weights, ConstantArray.of(RealScalar.ONE, 13));
  }

  @Test
  void testSamples() {
    assertEquals(SlidingWindow.samples(2), Tensors.fromString("{-1/4, 1/4}"));
    assertEquals(SlidingWindow.samples(3), Tensors.fromString("{-1/3, 0, 1/3}"));
    assertEquals(SlidingWindow.samples(4), Tensors.fromString("{-3/8, -1/8, 1/8, 3/8}"));
  }

  @Test
  void testSamplesDifferences() {
    for (int n = 2; n < 8; ++n) {
      Tensor vector = SlidingWindow.samples(n);
      Tensor result = Join.of(vector, vector.maps(RealScalar.ONE::add));
      assertEquals(Tally.of(Differences.of(result)).size(), 1);
    }
  }

  @Test
  void testZeroFail() {
    assertThrows(ArithmeticException.class, () -> SlidingWindow.weights(0, s -> s));
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}

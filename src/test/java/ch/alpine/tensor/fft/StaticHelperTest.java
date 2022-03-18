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

public class StaticHelperTest {
  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  public void testSimple(WindowFunctions windowFunctions) {
    int[] lengths = new int[] { 1, 2, 3, 4, 10, 32 };
    for (int windowLength : lengths) {
      Tensor weights = StaticHelper.weights(windowLength, windowFunctions.get());
      Tolerance.CHOP.requireClose(Total.of(weights), RealScalar.of(windowLength));
    }
  }

  @Test
  public void testDirichlet() {
    Tensor weights = StaticHelper.weights(13, DirichletWindow.FUNCTION);
    Tolerance.CHOP.requireClose(weights, ConstantArray.of(RealScalar.ONE, 13));
  }

  @Test
  public void testSamples() {
    assertEquals(StaticHelper.samples(2), Tensors.fromString("{-1/4, 1/4}"));
    assertEquals(StaticHelper.samples(3), Tensors.fromString("{-1/3, 0, 1/3}"));
    assertEquals(StaticHelper.samples(4), Tensors.fromString("{-3/8, -1/8, 1/8, 3/8}"));
  }

  @Test
  public void testSamplesDifferences() {
    for (int n = 2; n < 8; ++n) {
      Tensor vector = StaticHelper.samples(n);
      Tensor result = Join.of(vector, vector.map(RealScalar.ONE::add));
      assertEquals(Tally.of(Differences.of(result)).size(), 1);
    }
  }

  @Test
  public void testZeroFail() {
    assertThrows(ArithmeticException.class, () -> StaticHelper.weights(0, s -> s));
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}

// code by jph
package ch.ethz.idsc.tensor.fft;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    int[] lengths = new int[] { 1, 2, 3, 4, 10, 32 };
    for (WindowFunctions windowFunctions : WindowFunctions.values())
      for (int windowLength : lengths) {
        Tensor weights = StaticHelper.weights(windowLength, windowFunctions.get());
        Tolerance.CHOP.requireClose(Total.of(weights), RealScalar.of(windowLength));
      }
  }

  public void testDirichlet() {
    Tensor weights = StaticHelper.weights(13, DirichletWindow.FUNCTION);
    Tolerance.CHOP.requireClose(weights, ConstantArray.of(RealScalar.ONE, 13));
  }

  public void testSamples() {
    assertEquals(StaticHelper.samples(2), Tensors.fromString("{-1/4, 1/4}"));
    assertEquals(StaticHelper.samples(3), Tensors.fromString("{-1/3, 0, 1/3}"));
    assertEquals(StaticHelper.samples(4), Tensors.fromString("{-3/8, -1/8, 1/8, 3/8}"));
  }

  public void testSamplesDifferences() {
    for (int n = 2; n < 8; ++n) {
      Tensor vector = StaticHelper.samples(n);
      Tensor result = Join.of(vector, vector.map(RealScalar.ONE::add));
      assertEquals(Tally.of(Differences.of(result)).size(), 1);
    }
  }

  public void testZeroFail() {
    AssertFail.of(() -> StaticHelper.weights(0, s -> s));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}

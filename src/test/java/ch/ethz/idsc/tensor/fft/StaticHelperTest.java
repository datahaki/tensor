// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    int[] lengths = new int[] { 1, 10, 32 };
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
    assertEquals(StaticHelper.samples(4), Tensors.fromString("{-3/8, -1/8, 1/8, 3/8}"));
  }

  public void testZeroFail() {
    AssertFail.of(() -> StaticHelper.weights(0, s -> s));
  }
}

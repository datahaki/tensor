// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.io.IOException;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class WindowFunctionsTest extends TestCase {
  public void testSimple() {
    for (WindowFunctions windowFunction : WindowFunctions.values()) {
      ScalarUnaryOperator suo = windowFunction.get();
      assertEquals(suo.apply(RealScalar.of(-0.500001)), RealScalar.ZERO);
      assertEquals(suo.apply(RealScalar.of(+0.500001)), RealScalar.ZERO);
      Chop._15.requireClose(suo.apply(RealScalar.ZERO), RealScalar.ONE);
      assertTrue(suo.toString().startsWith(suo.getClass().getSimpleName()));
    }
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    for (WindowFunctions windowFunction : WindowFunctions.values())
      Serialization.copy(windowFunction.get());
  }

  public void testSymmetry() {
    Distribution distribution = UniformDistribution.of(-0.6, 0.6);
    for (WindowFunctions windowFunction : WindowFunctions.values())
      for (int count = 0; count < 10; ++count) {
        Scalar x = RandomVariate.of(distribution);
        Chop._15.requireClose(windowFunction.get().apply(x), windowFunction.get().apply(x.negate()));
      }
  }

  public void testInsideFail() {
    for (WindowFunctions windowFunction : WindowFunctions.values())
      AssertFail.of(() -> windowFunction.get().apply(Quantity.of(0.1, "s")));
  }

  public void testOustideFail() {
    for (WindowFunctions windowFunction : WindowFunctions.values())
      AssertFail.of(() -> windowFunction.get().apply(Quantity.of(1, "s")));
  }

  public void testComplexFail() {
    Scalar x = ComplexScalar.of(0.1, 0.2);
    for (WindowFunctions windowFunction : WindowFunctions.values())
      try {
        windowFunction.get().apply(x);
        System.out.println(windowFunction);
        fail();
      } catch (Exception e) {
        // ---
      }
  }
}

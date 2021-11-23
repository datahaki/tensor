// code by jph
package ch.alpine.tensor.sca.win;

import java.io.IOException;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
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
      } catch (Exception exception) {
        // ---
      }
  }
}

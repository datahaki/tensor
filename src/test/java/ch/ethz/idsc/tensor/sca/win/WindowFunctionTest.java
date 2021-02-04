// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class WindowFunctionTest extends TestCase {
  public void testSimple() {
    for (WindowFunction windowFunction : WindowFunction.values()) {
      assertEquals(windowFunction.apply(RealScalar.of(-0.500001)), RealScalar.ZERO);
      assertEquals(windowFunction.apply(RealScalar.of(+0.500001)), RealScalar.ZERO);
      Chop._15.requireClose(windowFunction.apply(RealScalar.ZERO), RealScalar.ONE);
      assertTrue(windowFunction.get().toString().startsWith(windowFunction.get().getClass().getSimpleName()));
    }
  }

  public void testSymmetry() {
    Distribution distribution = UniformDistribution.of(-0.6, 0.6);
    for (WindowFunction windowFunction : WindowFunction.values())
      for (int count = 0; count < 10; ++count) {
        Scalar x = RandomVariate.of(distribution);
        Chop._15.requireClose(windowFunction.apply(x), windowFunction.apply(x.negate()));
      }
  }

  public void testInsideFail() {
    for (WindowFunction windowFunction : WindowFunction.values())
      AssertFail.of(() -> windowFunction.apply(Quantity.of(0.1, "s")));
  }

  public void testOustideFail() {
    for (WindowFunction windowFunction : WindowFunction.values())
      AssertFail.of(() -> windowFunction.apply(Quantity.of(1, "s")));
  }

  public void testComplexFail() {
    Scalar x = ComplexScalar.of(0.1, 0.2);
    for (WindowFunction windowFunction : WindowFunction.values())
      try {
        windowFunction.apply(x);
        System.out.println(windowFunction);
        fail();
      } catch (Exception e) {
        // ---
      }
  }
}

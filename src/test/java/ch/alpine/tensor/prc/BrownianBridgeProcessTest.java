// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clips;

class BrownianBridgeProcessTest {
  @Test
  void test1() {
    BrownianBridgeProcess brownianBridgeProcess = BrownianBridgeProcess.of(Quantity.of(2, "m"));
    Distribution distribution = brownianBridgeProcess.at( //
        Clips.interval(3, 10), //
        Quantity.of(10, "m"), //
        Quantity.of(3, "m"), //
        RealScalar.of(4));
    assertEquals(Mean.of(distribution), Quantity.of(9, "m"));
    Tolerance.CHOP.requireClose(Variance.of(distribution), Quantity.of(24 / 7., "m^2"));
    assertEquals(brownianBridgeProcess.toString(), "BrownianBridgeProcess[2[m]]");
  }

  @Test
  void test2() {
    BrownianBridgeProcess brownianBridgeProcess = BrownianBridgeProcess.of(Quantity.of(1, "m*s^-1/2"));
    Distribution distribution = brownianBridgeProcess.at( //
        Clips.positive(Quantity.of(4, "s")), //
        Quantity.of(0, "m"), //
        Quantity.of(0, "m"), //
        Quantity.of(2, "s"));
    assertEquals(Mean.of(distribution), Quantity.of(0, "m"));
    assertEquals(StandardDeviation.of(distribution), Quantity.of(1, "m"));
  }

  @Test
  void testInvalid() {
    BrownianBridgeProcess brownianBridgeProcess = BrownianBridgeProcess.of(RealScalar.ONE);
    assertThrows(Exception.class, () -> brownianBridgeProcess.at( //
        Clips.unit(), //
        RealScalar.ZERO, //
        RealScalar.ZERO, //
        RealScalar.of(1.1)));
    assertThrows(Exception.class, () -> brownianBridgeProcess.at( //
        Clips.unit(), //
        RealScalar.ZERO, //
        RealScalar.ZERO, //
        RealScalar.of(-0.1)));
  }
}

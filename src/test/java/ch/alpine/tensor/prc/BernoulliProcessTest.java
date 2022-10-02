// code by jph
package ch.alpine.tensor.prc;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;

class BernoulliProcessTest {
  @Test
  void test() {
    RandomProcess randomProcess = BernoulliProcess.of(RationalScalar.HALF);
    TimeSeries timeSeries = TimeSeries.empty();
    randomProcess.eval(timeSeries, RealScalar.of(30));
    System.out.println(timeSeries.path());
  }
}

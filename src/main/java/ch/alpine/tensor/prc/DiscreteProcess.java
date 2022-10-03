// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Sign;

/** API EXPERIMENTAL */
public abstract class DiscreteProcess implements RandomProcess, Serializable {
  private final Distribution distribution;

  protected DiscreteProcess(Distribution distribution) {
    this.distribution = distribution;
  }

  @Override
  public final TimeSeries spawn() {
    return TimeSeries.empty(ResamplingMethods.NONE);
  }

  @Override
  public final Scalar eval(TimeSeries timeSeries, Random random, Scalar x) {
    IntegerQ.require(x);
    Sign.requirePositiveOrZero(x);
    if (timeSeries.isEmpty())
      timeSeries.insert(RealScalar.ZERO, RandomVariate.of(distribution, random));
    while (timeSeries.support().isOutside(x)) {
      Scalar ofs = timeSeries.support().max().add(RealScalar.ONE);
      timeSeries.insert(ofs, RandomVariate.of(distribution, random));
    }
    return (Scalar) timeSeries.eval(x);
  }
}

// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RenewalProcess.html">RenewalProcess</a> */
public class RenewalProcess implements RandomProcess, Serializable {
  /** @param distribution
   * @return */
  public static RandomProcess of(Distribution distribution) {
    return new RenewalProcess(Objects.requireNonNull(distribution));
  }

  // ---
  private final Distribution distribution;

  public RenewalProcess(Distribution distribution) {
    this.distribution = distribution;
  }

  @Override
  public Scalar eval(TimeSeries timeSeries, Random random, Scalar x) {
    Sign.requirePositiveOrZero(x);
    if (timeSeries.isEmpty()) {
      Scalar dt = RandomVariate.of(distribution, random);
      timeSeries.insert(dt.zero(), RealScalar.ZERO);
      timeSeries.insert(dt, RealScalar.ONE);
    }
    while (Scalars.lessEquals(timeSeries.support().max(), x)) {
      Scalar dt = RandomVariate.of(distribution, random);
      Scalar max = timeSeries.support().max();
      Tensor val = timeSeries.eval(max);
      timeSeries.insert(max.add(dt), val.add(RealScalar.ONE));
    }
    return (Scalar) timeSeries.eval(x);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("RenewalProcess", distribution);
  }
}

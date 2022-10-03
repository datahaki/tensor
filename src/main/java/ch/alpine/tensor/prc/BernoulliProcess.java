// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Sign;

public class BernoulliProcess implements RandomProcess, Serializable {
  public static RandomProcess of(Scalar p) {
    return new BernoulliProcess(p);
  }

  private final Scalar p;
  private Distribution distribution;

  private BernoulliProcess(Scalar p) {
    this.p = p;
    distribution = BernoulliDistribution.of(p);
  }

  @Override
  public Scalar eval(TimeSeries timeSeries, Random random, Scalar x) {
    IntegerQ.require(x);
    Sign.requirePositiveOrZero(x);
    if (timeSeries.isEmpty())
      timeSeries.insert(RealScalar.ZERO, RandomVariate.of(distribution, random));
    Clip clip = timeSeries.support();
    if (clip.isInside(x))
      return (Scalar) timeSeries.step(x);
    Scalar ofs = timeSeries.support().max();
    while (!ofs.equals(x)) {
      ofs = ofs.add(RealScalar.ONE);
      timeSeries.insert(ofs, RandomVariate.of(distribution, random));
    }
    return (Scalar) timeSeries.step(x);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("BernoulliProcess", p);
  }
}

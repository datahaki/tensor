// code by jph
package ch.alpine.tensor.pdf.c;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** functionality for generation of complex valued random variates that are
 * uniformly distributed in a disk centered at zero with given radius */
public record ComplexDiskUniformDistribution(Scalar radius) implements Distribution {
  private static final Distribution ARG = UniformDistribution.of(Clips.absolute(Pi.VALUE));

  /** @param radius positive
   * @return */
  public static Distribution of(Scalar radius) {
    return new ComplexDiskUniformDistribution(Sign.requirePositive(radius));
  }

  /** @param radius positive
   * @return */
  public static Distribution of(Number radius) {
    return of(RealScalar.of(radius));
  }

  // ---
  @Override
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return ComplexScalar.fromPolar( //
        Sqrt.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), randomGenerator)), //
        RandomVariate.of(ARG, randomGenerator)).multiply(radius);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("ComplexDiskUniformDistribution", radius);
  }
}

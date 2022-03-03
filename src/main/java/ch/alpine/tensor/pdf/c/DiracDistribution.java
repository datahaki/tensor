// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariateInterface;

/** @see UniformDistribution */
public class DiracDistribution implements Distribution, CDF, RandomVariateInterface, Serializable {
  /** @param value
   * @return */
  public static Distribution of(Scalar value) {
    return new DiracDistribution(Objects.requireNonNull(value));
  }

  // ---
  private final Scalar value;

  private DiracDistribution(Scalar value) {
    this.value = value;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Boole.of(Scalars.lessThan(value, x));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return Boole.of(Scalars.lessEquals(value, x));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return value;
  }
}

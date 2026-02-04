// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;

/** test scope only */
public enum ComplexNormalDistribution implements Distribution {
  STANDARD;

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return ComplexScalar.of(randomGenerator.nextGaussian(), randomGenerator.nextGaussian());
  }
}

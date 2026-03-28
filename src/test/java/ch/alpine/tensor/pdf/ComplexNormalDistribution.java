// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.Scalar;

/** test scope only */
public enum ComplexNormalDistribution implements Distribution {
  STANDARD;

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return Complex.of(randomGenerator.nextGaussian(), randomGenerator.nextGaussian());
  }
}

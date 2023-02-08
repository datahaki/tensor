// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;

/** test scope only */
public enum ComplexNormalDistribution implements Distribution, RandomVariateInterface {
  STANDARD;

  @Override // from RandomVariateInterface
  public Scalar randomVariate(RandomGenerator random) {
    return ComplexScalar.of(random.nextGaussian(), random.nextGaussian());
  }
}

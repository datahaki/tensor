// code by jph
package ch.alpine.tensor.pdf;

import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;

/** test scope only */
public enum ComplexNormalDistribution implements Distribution, RandomVariateInterface {
  STANDARD;

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return ComplexScalar.of(random.nextGaussian(), random.nextGaussian());
  }
}

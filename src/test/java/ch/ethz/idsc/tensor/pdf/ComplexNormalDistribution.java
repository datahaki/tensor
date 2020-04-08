// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.util.Random;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum ComplexNormalDistribution implements Distribution, RandomVariateInterface {
  STANDARD;

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return ComplexScalar.of(random.nextGaussian(), random.nextGaussian());
  }
}

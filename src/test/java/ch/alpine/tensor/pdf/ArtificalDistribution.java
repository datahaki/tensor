// code by jph
package ch.alpine.tensor.pdf;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class ArtificalDistribution implements Distribution, RandomVariateInterface {
  private int a = 0;

  @Override
  public Scalar randomVariate(Random random) {
    a += random.nextInt(10);
    a %= 20;
    return RealScalar.of(a);
  }
}

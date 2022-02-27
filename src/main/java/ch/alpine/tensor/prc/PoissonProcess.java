// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonProcess.html">PoissonProcess</a> */
/* package */ class PoissonProcess implements RandomProcess, Serializable {
  private final Distribution distribution;
  private int count = -1;

  public PoissonProcess(Scalar lambda) {
    distribution = ExponentialDistribution.of(lambda);
  }

  /** @param random
   * @return {time, count} */
  public Tensor next(Random random) {
    return Tensors.of(RandomVariate.of(distribution, random), RealScalar.of(++count));
  }

  @Override
  public Tensor path() {
    // TODO Auto-generated method stub
    return null;
  }
}

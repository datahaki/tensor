// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Beta;

public class DirichletDistribution {
  @SuppressWarnings("unused")
  private final Tensor alphas;
  @SuppressWarnings("unused")
  private final Scalar factor;

  public DirichletDistribution(Tensor alphas) {
    this.alphas = alphas;
    factor = Beta.of(alphas);
  }

  public Tensor at(Tensor x) {
    return null;
  }
}

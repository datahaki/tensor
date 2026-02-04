package ch.alpine.tensor.mat.cd;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

public class MultinormalDistribution implements RandomSampleInterface, Serializable {
  /** @param sigma covariance matrix
   * @return */
  public static RandomSampleInterface of(Tensor sigma) {
    return new MultinormalDistribution(CholeskyDecomposition.of(sigma));
  }

  // ---
  private final Tensor L;

  public MultinormalDistribution(CholeskyDecomposition choleskyDecomposition) {
    L = choleskyDecomposition.getL();
  }

  @Override
  public Tensor randomSample(RandomGenerator randomGenerator) {
    return L.dot(RandomVariate.of(NormalDistribution.standard(), randomGenerator, L.length()));
  }
}

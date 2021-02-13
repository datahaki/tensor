// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.nrm.Norm;
import ch.ethz.idsc.tensor.nrm.Normalize;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;

/** iterative method to find solution to Fermat-Weber Problem
 * iteration based on Endre Vaszonyi Weiszfeld
 * 
 * <p>implementation based on
 * "Weiszfeld’s Method: Old and New Results"
 * by Amir Beck, Shoham Sabach */
public class WeiszfeldMethod implements SpatialMedian, Serializable {
  private static final long serialVersionUID = -555862284852117669L;
  private static final int MAX_ITERATIONS = 512;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  /** @param chop non null
   * @return */
  public static SpatialMedian with(Chop chop) {
    return new WeiszfeldMethod(Objects.requireNonNull(chop));
  }

  /***************************************************/
  private final Chop chop;

  /** @param chop */
  private WeiszfeldMethod(Chop chop) {
    this.chop = chop;
  }

  @Override // from SpatialMedian
  public Optional<Tensor> uniform(Tensor sequence) {
    return weighted(sequence, ConstantArray.of(RationalScalar.of(1, sequence.length()), sequence.length()));
  }

  @Override // from SpatialMedian
  public Optional<Tensor> weighted(Tensor sequence, Tensor weights) {
    Tolerance.CHOP.requireClose(Total.of(weights), RealScalar.ONE);
    Tensor point = N.DOUBLE.of(weights.dot(sequence)); // initial value
    int iteration = 0;
    while (++iteration < MAX_ITERATIONS) {
      Tensor prev = point;
      Tensor dist = Tensor.of(sequence.stream().map(prev::subtract).map(Norm._2::ofVector));
      int index = ArgMin.of(dist);
      if (Scalars.isZero(dist.Get(index)))
        return Optional.of(point);
      Tensor invdist = dist.map(Scalar::reciprocal);
      point = NORMALIZE.apply(weights.pmul(invdist)).dot(sequence);
      if (chop.isClose(point, prev))
        return Optional.of(point);
    }
    return Optional.empty();
  }
}

// code by jph
package ch.ethz.idsc.tensor.opt;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.UnaryOperator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;

/** iterative method to find solution to Fermat-Weber Problem
 * iteration based on Endre Vaszonyi Weiszfeld
 * 
 * <p>implementation based on
 * "Weiszfeld’s Method: Old and New Results"
 * by Amir Beck, Shoham Sabach */
/* package */ class WeiszfeldMethod implements SpatialMedian, Serializable {
  private static final int MAX_ITERATIONS = 512;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
  public static final SpatialMedian DEFAULT = new WeiszfeldMethod(Tolerance.CHOP);
  /***************************************************/
  private final Chop chop;

  /** @param chop */
  public WeiszfeldMethod(Chop chop) {
    this.chop = chop;
  }

  @Override // from SpatialMedian
  public Optional<Tensor> uniform(Tensor points) {
    return minimum(points, t -> t);
  }

  @Override // from SpatialMedian
  public Optional<Tensor> weighted(Tensor points, Tensor weights) {
    return minimum(points, weights::pmul);
  }

  private Optional<Tensor> minimum(Tensor points, UnaryOperator<Tensor> unaryOperator) {
    Tensor point = N.DOUBLE.of(Mean.of(unaryOperator.apply(points))); // initial value
    int iteration = 0;
    while (++iteration < MAX_ITERATIONS) {
      Tensor prev = point;
      Tensor dist = Tensor.of(points.stream().map(prev::subtract).map(Norm._2::ofVector));
      int index = ArgMin.of(dist);
      if (Scalars.isZero(dist.Get(index)))
        return Optional.of(point.copy());
      Tensor invdist = dist.map(Scalar::reciprocal);
      point = NORMALIZE.apply(unaryOperator.apply(invdist)).dot(points);
      if (chop.close(point, prev))
        return Optional.of(point);
    }
    return Optional.empty();
  }
}

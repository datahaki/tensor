// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.Iterator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.AdjacentReduce;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** the implementation uses quadratic extrapolation at the boundaries
 * the weighting is in linear space
 * 
 * @see github/datahaki/sophis/Curvature2D
 * @see AdjacentReduce */
public abstract class TripleReduceExtrapolation implements TensorUnaryOperator {
  private static final InterpolatingPolynomial INTERPOLATING_POLYNOMIAL = //
      InterpolatingPolynomial.of(Tensors.vector(1, 2, 3));
  private static final Scalar LAST = RealScalar.of(4);

  /** @param points of the form {{p1x, p1y}, {p2x, p2y}, ..., {pNx, pNy}}
   * @return vector with same length as points */
  @Override
  public final Tensor apply(Tensor points) {
    int length = points.length();
    // TODO SOPHUS UNIT for length <= 2 might not produce the right units
    Tensor vector = Array.zeros(length);
    if (2 < length) {
      Iterator<Tensor> iterator = points.iterator();
      Tensor p = iterator.next();
      Tensor q = iterator.next();
      int index = 0;
      while (iterator.hasNext())
        vector.set(reduce(p, p = q, q = iterator.next()), ++index);
      int last = length - 1;
      if (4 < length) {
        vector.set(INTERPOLATING_POLYNOMIAL.scalarUnaryOperator(vector.extract(1, 4)).apply(RealScalar.ZERO), 0);
        vector.set(INTERPOLATING_POLYNOMIAL.scalarUnaryOperator(vector.extract(length - 4, last)).apply(LAST), last);
      } else {
        vector.set(vector.Get(1), 0);
        vector.set(vector.Get(length - 2), last);
      }
    }
    return vector;
  }

  /** @param p
   * @param q
   * @param r
   * @return */
  protected abstract Scalar reduce(Tensor p, Tensor q, Tensor r);
}

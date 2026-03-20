// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.Arrays;
import java.util.Iterator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.AdjacentReduce;
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

  /** @param sequence
   * @return vector with same length as points */
  @Override
  public final Tensor apply(Tensor sequence) {
    int length = sequence.length();
    Tensor[] tensor = new Tensor[length];
    if (2 < length) {
      Iterator<Tensor> iterator = sequence.iterator();
      Tensor p = iterator.next();
      Tensor q = iterator.next();
      int index = 0;
      while (iterator.hasNext())
        tensor[++index] = reduce(p, p = q, q = iterator.next());
      int last = length - 1;
      if (4 < length) {
        Tensor prefix = Tensors.of(tensor[1], tensor[2], tensor[3]);
        tensor[0] = INTERPOLATING_POLYNOMIAL.scalarTensorFunction(prefix).apply(RealScalar.ZERO);
        Tensor suffix = Tensors.of(tensor[last - 3], tensor[last - 2], tensor[last - 1]);
        tensor[last] = INTERPOLATING_POLYNOMIAL.scalarTensorFunction(suffix).apply(LAST);
      } else {
        tensor[0] = tensor[1];
        tensor[last] = tensor[length - 2];
      }
    } else
      throw new RuntimeException();
    return Tensor.of(Arrays.stream(tensor));
  }

  /** @param p
   * @param q
   * @param r
   * @return */
  protected abstract Tensor reduce(Tensor p, Tensor q, Tensor r);
}

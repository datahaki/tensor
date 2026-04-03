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
 * @see repo github/datahaki/sophis Curvature2D
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
    if (length < 3)
      return petite(sequence);
    Tensor[] tensor = new Tensor[length];
    {
      Iterator<Tensor> iterator = sequence.iterator();
      Tensor p = iterator.next();
      Tensor q = iterator.next();
      int index = 0;
      while (iterator.hasNext())
        tensor[++index] = reduce(p, p = q, q = iterator.next());
    }
    int zero = 0;
    int last = length - 1;
    if (5 <= length) {
      Tensor prefix = Tensors.of(tensor[zero + 1], tensor[zero + 2], tensor[zero + 3]);
      tensor[zero] = INTERPOLATING_POLYNOMIAL.scalarTensorFunction(prefix).apply(RealScalar.ZERO);
      Tensor suffix = Tensors.of(tensor[last - 3], tensor[last - 2], tensor[last - 1]);
      tensor[last] = INTERPOLATING_POLYNOMIAL.scalarTensorFunction(suffix).apply(LAST);
    } else {
      tensor[zero] = tensor[zero + 1].copy();
      tensor[last] = tensor[last - 1].copy();
    }
    return Tensor.of(Arrays.stream(tensor));
  }

  /** @param p
   * @param q
   * @param r
   * @return */
  protected abstract Tensor reduce(Tensor p, Tensor q, Tensor r);

  /** @param sequence of length 0, 1, or 2
   * @return */
  protected abstract Tensor petite(Tensor sequence);
}

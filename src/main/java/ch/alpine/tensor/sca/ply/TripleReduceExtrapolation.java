// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.Iterator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.AdjacentReduce;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.MatrixGradient;

/** the implementation uses quadratic extrapolation at the boundaries
 * the weighting is in linear space
 * 
 * @see repo github/datahaki/sophis Curvature2D
 * @see AdjacentReduce
 * @see MatrixGradient */
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
    Tensor[] tensors = new Tensor[length];
    {
      Iterator<Tensor> iterator = sequence.iterator();
      Tensor p = iterator.next();
      Tensor q = iterator.next();
      int index = 0;
      while (iterator.hasNext())
        tensors[++index] = reduce(p, p = q, q = iterator.next());
    }
    int zero = 0;
    int last = length - 1;
    if (5 <= length) {
      Tensor prefix = Unprotect.byRef(tensors[zero + 1], tensors[zero + 2], tensors[zero + 3]);
      tensors[zero] = INTERPOLATING_POLYNOMIAL.scalarTensorFunction(prefix).apply(RealScalar.ZERO);
      Tensor suffix = Unprotect.byRef(tensors[last - 3], tensors[last - 2], tensors[last - 1]);
      tensors[last] = INTERPOLATING_POLYNOMIAL.scalarTensorFunction(suffix).apply(LAST);
    } else {
      tensors[zero] = tensors[zero + 1].copy();
      tensors[last] = tensors[last - 1].copy();
    }
    return Unprotect.byRef(tensors);
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

// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;

/** ArcTanh[z] == 1/2 (log(1+z)-log(1-z))
 * 
 * <pre>
 * ArcTanh[NaN] == NaN
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArcTanh.html">ArcTanh</a>
 * 
 * @see Tanh */
public enum ArcTanh implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return RationalScalar.HALF.multiply( //
        Log.FUNCTION.apply(RealScalar.ONE.add(scalar)).subtract( //
            Log.FUNCTION.apply(RealScalar.ONE.subtract(scalar))));
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their arc tanh */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}

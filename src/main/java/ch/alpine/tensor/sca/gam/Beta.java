// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.exp.Exp;

/** Reference:
 * "Gamma, Beta, and Related Functions" in NR, 2007
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Beta.html">Beta</a> */
public enum Beta {
  ;
  /** @param scalars
   * @return */
  public static Scalar of(Scalar... scalars) {
    return of(Tensors.of(scalars));
  }

  /** @param numbers
   * @return */
  public static Scalar of(Number... numbers) {
    return of(Tensors.vector(numbers));
  }

  /** @param vector
   * @return */
  public static Scalar of(Tensor vector) {
    return Exp.FUNCTION.apply( //
        Total.ofVector(vector.map(LogGamma.FUNCTION)).subtract(LogGamma.FUNCTION.apply(Total.ofVector(vector))));
  }
}

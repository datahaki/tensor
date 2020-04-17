// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;

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

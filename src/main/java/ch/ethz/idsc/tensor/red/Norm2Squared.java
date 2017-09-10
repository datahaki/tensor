// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.AbsSquared;

/** the square of Norm._2 is not a norm therefore the class does not
 * implement {@link NormInterface}.
 * 
 * @see Norm
 * @see AbsSquared */
public enum Norm2Squared {
  ;
  // ---
  public static Scalar ofVector(Tensor vector) {
    return vector.stream() //
        .map(Scalar.class::cast) //
        .map(AbsSquared.FUNCTION) //
        .reduce(Scalar::add).get();
  }

  public static Scalar ofMatrix(Tensor matrix) {
    Scalar value = Norm._2.ofMatrix(matrix);
    return value.multiply(value);
  }
}

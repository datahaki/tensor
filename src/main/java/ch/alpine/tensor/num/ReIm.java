// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ReIm.html">ReIm</a> */
public enum ReIm {
  ;
  /** @param z
   * @return vector {Real[z], Imag[z]} */
  public static Tensor of(Scalar z) {
    return Tensors.of( //
        Re.FUNCTION.apply(z), //
        Im.FUNCTION.apply(z));
  }
}

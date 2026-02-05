// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ReIm.html">ReIm</a> */
public record ReIm(Scalar re, Scalar im) implements Serializable {
  /** @param z
   * @return */
  public static ReIm of(Scalar z) {
    return new ReIm( //
        Re.FUNCTION.apply(z), //
        Im.FUNCTION.apply(z));
  }

  /** @param re
   * @param im
   * @param z_re
   * @param z_im
   * @return emulation of complex multiplication (x + y*i) (c + s*i) */
  public static ReIm product(Scalar re, Scalar im, Scalar z_re, Scalar z_im) {
    return new ReIm( //
        re.multiply(z_re).subtract(im.multiply(z_im)), //
        im.multiply(z_re).add(re.multiply(z_im)));
  }

  // ---
  /** complex multiplication between z and vector[0]+i*vector[1]
   * 
   * @param vector of length 2 with entries that may be {@link Quantity}
   * @return vector of length 2 with real entries corresponding to real and imag of result */
  public Tensor rotate(Tensor vector) {
    Integers.requireEquals(vector.length(), 2);
    return product(re, im, vector.Get(0), vector.Get(1)).vector();
  }

  /** function recreates API of Mathematica
   * however, not used in Java programs
   * 
   * @return vector {Re[z], Im[z]} */
  public Tensor vector() {
    return Tensors.of(re, im);
  }
}

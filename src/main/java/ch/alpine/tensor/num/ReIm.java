// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ReIm.html">ReIm</a> */
public class ReIm implements Serializable {
  /** @param z
   * @return vector {Re[z], Im[z]} */
  public static Tensor of(Scalar z) {
    return Tensor.of(stream(z));
  }

  /** @param z
   * @return stream consisting of the two scalars Re[z], and Im[z] */
  public static Stream<Tensor> stream(Scalar z) {
    return Stream.of( //
        Re.FUNCTION.apply(z), //
        Im.FUNCTION.apply(z));
  }

  // ---
  private final Scalar re;
  private final Scalar im;

  public ReIm(Scalar scalar) {
    re = Re.FUNCTION.apply(scalar);
    im = Im.FUNCTION.apply(scalar);
  }

  public Scalar re() {
    return re;
  }

  public Scalar im() {
    return im;
  }
}

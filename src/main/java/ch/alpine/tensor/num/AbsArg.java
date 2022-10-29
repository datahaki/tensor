// code by jph
package ch.alpine.tensor.num;

import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Arg;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/AbsArg.html">AbsArg</a> */
public enum AbsArg {
  ;
  /** @param z
   * @return vector {Abs[z], Arg[z]} */
  public static Tensor of(Scalar z) {
    return Tensor.of(stream(z));
  }

  /** @param z
   * @return stream consisting of the two scalars Re[z], and Im[z] */
  public static Stream<Tensor> stream(Scalar z) {
    return Stream.of( //
        Abs.FUNCTION.apply(z), //
        Arg.FUNCTION.apply(z));
  }
}

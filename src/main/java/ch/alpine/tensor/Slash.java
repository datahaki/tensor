// code by jph
package ch.alpine.tensor;

import java.util.function.Function;

/** inspired by mathematica's /@ i.e. "slash at" */
public enum Slash {
  ;
  /** slash corresponds to Mathematica's
   * <pre>
   * /@
   * </pre>
   *
   * @param tensor
   * @return {tuo(tensor.get(0)), tuo(tensor.get(1)), ..., tuo(tensor.get(length-1)))} */
  public static Tensor of(Tensor tensor, Function<Tensor, ? extends Tensor> tuo) {
    return Unprotect.of(tensor.length(), tensor.stream().map(tuo));
  }

  public static Tensor vector(Tensor vector, Function<Scalar, ? extends Tensor> stf) {
    return Unprotect.of(vector.length(), vector.stream().map(Scalar.class::cast).map(stf));
  }
}

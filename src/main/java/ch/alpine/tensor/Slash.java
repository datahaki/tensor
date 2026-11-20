// code by jph
package ch.alpine.tensor;

import java.util.function.Function;

/** inspired by mathematica's /@ i.e. "slash at" */
public enum Slash {
  ;
  public static Tensor of(Tensor tensor, Function<Tensor, ? extends Tensor> tuo) {
    return Unprotect.of(tensor.length(), tensor.stream().map(tuo));
  }
}

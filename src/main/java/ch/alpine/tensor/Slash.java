// code by jph
package ch.alpine.tensor;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

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
    return Tensor.of(tensor.stream().map(tuo), tensor.length());
  }

  public static Tensor vector(Tensor vector, Function<Scalar, ? extends Tensor> stf) {
    return Tensor.of(vector.stream().map(Scalar.class::cast).map(stf), vector.length());
  }

  public static Tensor of(int[] array, IntFunction<? extends Tensor> itf) {
    return Tensor.of(Arrays.stream(array).mapToObj(itf), array.length);
  }

  public static <T> Tensor of(T[] array, Function<T, ? extends Tensor> itf) {
    return Tensor.of(Arrays.stream(array).map(itf), array.length);
  }
}

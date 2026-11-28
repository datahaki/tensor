// code by jph
package ch.alpine.tensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;

import ch.alpine.tensor.ext.Integers;

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
    List<Tensor> list = new ArrayList<>(tensor.length());
    for (Tensor elem : tensor)
      list.add(tuo.apply(elem));
    return Unprotect.using(list);
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

  /** @param tbo
   * @param a
   * @param b
   * @return */
  public static Tensor of(BinaryOperator<Tensor> tbo, Tensor a, Tensor b) {
    int length = Integers.requireEquals(a.length(), b.length());
    Iterator<Tensor> ia = a.iterator();
    Iterator<Tensor> ib = b.iterator();
    List<Tensor> list = new ArrayList<>(length);
    while (ia.hasNext())
      list.add(tbo.apply(ia.next(), ib.next()));
    return Unprotect.using(list);
  }

  public static Tensor reduce(BinaryOperator<Tensor> tbo, Tensor a, Tensor b) {
    Integers.requireEquals(a.length(), b.length());
    Iterator<Tensor> ia = a.iterator();
    Iterator<Tensor> ib = b.iterator();
    Tensor first = tbo.apply(ia.next(), ib.next());
    while (ia.hasNext())
      first = tbo.apply(first, tbo.apply(ia.next(), ib.next()));
    return first;
  }

  public static Tensor reduce(BinaryOperator<Tensor> tbo, BiFunction<Tensor, Scalar, Tensor> bif, Tensor a, Tensor b) {
    Integers.requireEquals(a.length(), b.length());
    Iterator<Tensor> ia = a.iterator();
    Iterator<Tensor> ib = b.iterator();
    Tensor first = bif.apply(ia.next(), (Scalar) ib.next());
    while (ia.hasNext())
      first = tbo.apply(first, bif.apply(ia.next(), (Scalar) ib.next()));
    return first;
  }
}

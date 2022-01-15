// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.mat.NilpotentMatrixQ;

public enum NilpotentAlgebraQ {
  ;
  private static final int SIZE = 16;
  private static final Cache<Tensor, Boolean> CACHE = Cache.of(NilpotentAlgebraQ::build, SIZE);

  private static Boolean build(Tensor ad) {
    return IntStream.range(0, ad.length()) //
        .allMatch(i -> NilpotentMatrixQ.of(ad.get(Tensor.ALL, Tensor.ALL, i)));
  }

  /** @param ad
   * @return whether lie algebra defined by given ad-tensor is nilpotent */
  public static boolean of(Tensor ad) {
    return CACHE.apply(ad);
  }
}

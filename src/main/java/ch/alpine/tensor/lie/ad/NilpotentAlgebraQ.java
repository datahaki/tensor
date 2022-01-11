// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.NilpotentMatrixQ;

public enum NilpotentAlgebraQ {
  ;
  /** @param ad
   * @return whether lie algebra defined by given ad-tensor is nilpotent */
  public static boolean of(Tensor ad) {
    int n = ad.length();
    return IntStream.range(0, n) //
        .allMatch(i -> NilpotentMatrixQ.of(ad.get(Tensor.ALL, Tensor.ALL, i)));
  }
}

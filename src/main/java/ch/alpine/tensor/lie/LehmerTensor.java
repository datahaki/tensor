// code by jph
package ch.alpine.tensor.lie;

import java.util.Collections;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;

/** definition of Lehmer tensor taken from [Qi, Luo 2017] p.256 */
public enum LehmerTensor {
  ;
  /** @param d positive
   * @return tensor of rank d and dimensions d x d x ... x d */
  public static Tensor of(int d) {
    return Array.of(list -> RationalScalar.of( //
        list.stream().reduce(Math::min).get() + 1, //
        list.stream().reduce(Math::max).get() + 1), Collections.nCopies(d, d));
  }

  /** @param vector of length d with positive coefficients
   * @return tensor of rank d and dimensions d x d x ... x d */
  public static Tensor of(Tensor vector) {
    int n = vector.length();
    return Array.of(list -> //
    list.stream().map(vector::Get).reduce(Min::of).get().divide( //
        list.stream().map(vector::Get).reduce(Max::of).get()), Collections.nCopies(n, n));
  }
}

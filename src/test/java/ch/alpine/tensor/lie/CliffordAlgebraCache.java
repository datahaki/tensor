// code by jph
package ch.alpine.tensor.lie;

import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.Integers;

public enum CliffordAlgebraCache {
  ;
  private static final int MAX_SIZE = 12;
  private static final Cache<List<Integer>, CliffordAlgebra> CACHE = Cache.of(list -> {
    int p = list.get(0);
    int q = list.get(1);
    if (q == 0)
      return CliffordAlgebra.positive(p);
    if (p == 0)
      return CliffordAlgebra.negative(q);
    return CliffordAlgebra.of(p, q);
  }, MAX_SIZE);

  public static CliffordAlgebra of(int p, int q) {
    return CACHE.apply(Arrays.asList( //
        Integers.requirePositiveOrZero(p), //
        Integers.requirePositiveOrZero(q)));
  }

  /** @param p non-negative
   * @return Cl(p, 0) */
  public static CliffordAlgebra positive(int p) {
    return of(p, 0);
  }

  /** @param q non-negative
   * @return Cl(0, q) */
  public static CliffordAlgebra negative(int q) {
    return of(0, q);
  }
}

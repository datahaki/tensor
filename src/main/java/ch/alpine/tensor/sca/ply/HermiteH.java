// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;

/** physicist's Hermite polynomials
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Hermite_polynomials
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HermiteH.html">HermiteH</a> */
public enum HermiteH {
  ;
  private static final Polynomial H1 = Polynomial.of(Tensors.of(RealScalar.ZERO, RealScalar.TWO));
  private static final List<Polynomial> LIST = new ArrayList<>();
  static {
    LIST.add(Polynomial.of(Tensors.of(RealScalar.ONE)));
    LIST.add(H1);
  }

  /** @param n non-negative
   * @return */
  public static Polynomial of(int n) {
    if (LIST.size() <= Integers.requirePositiveOrZero(n))
      synchronized (LIST) {
        while (LIST.size() <= n) {
          int i = LIST.size() - 1;
          LIST.add(LIST.get(i).times(H1).plus(LIST.get(i - 1).times(RealScalar.of(-i - i))));
        }
      }
    return LIST.get(n);
  }
}

// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;

/** Chebyshev polynomials
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Chebyshev_polynomials */
public enum Chebyshev {
  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/ChebyshevT.html">ChebyshevT</a> */
  T(1),
  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/ChebyshevU.html">ChebyshevU</a> */
  U(2);

  private static final Polynomial _2_X = Polynomial.of(Tensors.vector(0, 2));
  // ---
  private final List<Polynomial> list = new ArrayList<>();

  Chebyshev(int seed) {
    list.add(Polynomial.of(Tensors.vector(1)));
    list.add(Polynomial.of(Tensors.vector(0, seed)));
  }

  /** @param index non-negative
   * @return Chebyshev polynomial */
  public Polynomial of(int index) {
    if (list.size() <= Integers.requirePositiveOrZero(index))
      synchronized (this) {
        Polynomial prev = list.get(list.size() - 2);
        Polynomial next = list.get(list.size() - 1);
        while (list.size() <= index) {
          Polynomial push = next.times(_2_X).minus(prev);
          list.add(push);
          prev = next;
          next = push;
        }
      }
    return list.get(index);
  }
}

// code by jph
package ch.alpine.tensor.num;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;

public enum ChebyshevT {
  ;
  private static final List<Polynomial> MEMO = new ArrayList<>();
  static {
    MEMO.add(Polynomial.of(Tensors.vector(1)));
    MEMO.add(Polynomial.of(Tensors.vector(0, 1)));
  }

  /** @param index
   * @return */
  public static Polynomial of(int index) {
    if (MEMO.size() <= Integers.requirePositiveOrZero(index))
      synchronized (MEMO) {
        Polynomial prev = MEMO.get(MEMO.size() - 2);
        Polynomial next = MEMO.get(MEMO.size() - 1);
        while (MEMO.size() <= index) {
          Polynomial push = next.times(Polynomial.of(Tensors.vector(0, 2))).minus(prev);
          MEMO.add(push);
          prev = next;
          next = push;
        }
      }
    return MEMO.get(index);
  }
}

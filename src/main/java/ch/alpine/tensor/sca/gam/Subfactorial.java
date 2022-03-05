// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** Reference:
 * https://www.geeksforgeeks.org/find-subfactorial-of-a-number/
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Subfactorial.html">Subfactorial</a> */
public enum Subfactorial implements ScalarUnaryOperator {
  FUNCTION;

  /** MEMO[index] == Subfactorial[index] */
  private static final Tensor MEMO = Tensors.vector(1, 0);

  /** @param index
   * @return Factorial2[index]
   * @throws Exception if index is negative */
  public static Scalar of(int index) {
    if (MEMO.length() <= Integers.requirePositiveOrZero(index))
      synchronized (FUNCTION) {
        for (int i = MEMO.length(); i <= index; ++i)
          MEMO.append(MEMO.Get(i - 1).add(MEMO.Get(i - 2)).multiply(RealScalar.of(i - 1)));
      }
    return MEMO.Get(index);
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return of(Scalars.intValueExact(scalar));
  }
}

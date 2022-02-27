// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** In Mathematica Factorial2[x] can be abbreviated as x!!
 *
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Factorial2.html">Factorial2</a> */
public enum Factorial2 implements ScalarUnaryOperator {
  FUNCTION;

  private static final Tensor MEMO = Tensors.vector(1, 1);

  /** @param index non-negative
   * @return
   * @throws Exception if index is negative */
  public static Scalar of(int index) {
    if (index == -1)
      return RealScalar.ONE;
    if (MEMO.length() <= Integers.requirePositiveOrZero(index))
      synchronized (FUNCTION) {
        for (int i = MEMO.length(); i <= index; ++i)
          MEMO.append(MEMO.Get(i - 2).multiply(RealScalar.of(i)));
      }
    return MEMO.Get(index);
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return of(Scalars.intValueExact(scalar));
  }
}

// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.DoubleScalar;
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

  /** MEMO[index] == Factorial2[index] */
  private static final Tensor MEMO = Tensors.vector(1, 1);

  /** @param index
   * @return Factorial2[index] */
  public static Scalar of(int index) {
    if (index < 0) {
      if (index == -1)
        return RealScalar.ONE;
      if (index % 2 == 0)
        return DoubleScalar.INDETERMINATE; // ComplexInfinity
      Scalar scalar = of(-index - 2).reciprocal();
      return ((index + 1) / 2) % 2 == 0 //
          ? scalar
          : scalar.negate();
    }
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

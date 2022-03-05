// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** the tensor library defines factorial only for non-negative integers.
 * For input with decimal or complex numbers use {@link Gamma}.
 * 
 * Mathematica::FunctionExpand[x!] == Gamma[1 + x]
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Factorial.html">Factorial</a> */
public enum Factorial implements ScalarUnaryOperator {
  FUNCTION;

  private static final Tensor MEMO = Tensors.vector(1); // initialize value for 0!

  /** @param scalar non-negative integer
   * @return factorial of given scalar
   * @throws Exception if scalar is not a non-negative integer */
  @Override
  public Scalar apply(Scalar scalar) {
    return of(Scalars.intValueExact(scalar));
  }

  /** @param index non-negative
   * @return factorial of index
   * @throws Exception if index is negative */
  public static Scalar of(int index) {
    if (MEMO.length() <= Integers.requirePositiveOrZero(index))
      synchronized (FUNCTION) {
        Scalar x = Last.of(MEMO);
        while (MEMO.length() <= index)
          MEMO.append(x = x.multiply(RealScalar.of(MEMO.length())));
      }
    return MEMO.Get(index);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their factorial */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}

// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Prime.html">Prime</a> */
public enum Prime {
  ;
  private static final Tensor LIST = Tensors.vector(2, 3);

  /** Examples:
   * Prime[1] == 2
   * Prime[2] == 3
   * Prime[3] == 5
   * ...
   * 
   * Careful:
   * function allocates memory and requires computation time!
   * 
   * @param n strictly positive
   * @return n-th prime
   * @throws Exception if n is not positive, or n-th prime is cannot be provided by implementation */
  public static Scalar of(int n) {
    if (LIST.length() < n)
      synchronized (LIST) {
        BigInteger last = Scalars.bigIntegerValueExact(Last.of(LIST));
        while (LIST.length() < n)
          LIST.append(RealScalar.of(last = last.nextProbablePrime()));
      }
    return LIST.Get(n - 1);
  }
}

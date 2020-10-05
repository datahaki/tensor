// code by jph
// implementation adapted from Ruby code of https://en.wikipedia.org/wiki/Exponentiation_by_squaring
package ch.ethz.idsc.tensor.num;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/** exponentiation with integer exponents
 * 
 * implementation uses exponentiation by squaring
 * 
 * interface used by MatrixPower, RationalScalar, ComplexScalar, and GaussScalar
 * 
 * @param <T> may also be Integer etc. */
public final class BinaryPower<T> implements Serializable {
  private static final long serialVersionUID = -1557915168292102268L;
  private final GroupInterface<T> groupInterface;

  public BinaryPower(GroupInterface<T> groupInterface) {
    this.groupInterface = Objects.requireNonNull(groupInterface);
  }

  /** @param x
   * @param exponent
   * @return x to the power of the given exponent */
  public T raise(T x, BigInteger exponent) {
    T result = groupInterface.identity();
    if (exponent.signum() == 0)
      return result;
    if (exponent.signum() == -1) { // convert problem to positive exponent
      exponent = exponent.negate();
      x = groupInterface.invert(x);
    }
    // the below implementation was adapted from
    // https://en.wikipedia.org/wiki/Exponentiation_by_squaring
    // Section: Computation by powers of 2
    // non-recursive implementation of the algorithm in Ruby
    while (true) { // iteration
      if (exponent.testBit(0))
        result = groupInterface.combine(x, result);
      exponent = exponent.shiftRight(1);
      if (exponent.signum() == 0)
        return result;
      x = groupInterface.combine(x, x);
    }
  }
}

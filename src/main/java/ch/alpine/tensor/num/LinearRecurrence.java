// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.ex.MatrixPower;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearRecurrence.html">LinearRecurrence</a> */
public class LinearRecurrence implements Serializable {
  private final Tensor reverse;
  private final int n;
  private final Tensor init;
  private final Tensor matrix;
  private final int last;

  /** @param kernel vector
   * @param init vector of same length as kernel */
  public LinearRecurrence(Tensor kernel, Tensor init) {
    this.reverse = Reverse.of(VectorQ.require(kernel));
    n = reverse.length();
    this.init = VectorQ.requireLength(init, n);
    matrix = Array.same(reverse.Get(0).zero(), n, n);
    last = n - 1;
    matrix.set(reverse, last);
    Scalar one = reverse.Get(0).one();
    for (int i = 0; i < last; ++i)
      matrix.set(one, i, i + 1);
  }

  /** Careful: index differs by 1 when compared with Mathematica
   * 
   * @param index non-negative
   * @return */
  public Scalar at(long index) {
    if (index < 0)
      throw new IllegalArgumentException("" + index);
    return index < n //
        ? init.Get((int) index)
        : MatrixPower.of(matrix, index - last).dot(init).Get(last);
  }

  /** @param length
   * @return vector of given length with the starting elements coincide with init */
  public Tensor until(int length) {
    if (length <= n)
      return init.extract(0, length);
    Tensor vector = Join.of(init, ConstantArray.of(init.Get(0).zero(), length - n));
    for (int i = n; i < length; ++i)
      vector.set(reverse.dot(vector.extract(i - n, i)), i);
    return vector;
  }

  /** @return */
  public Tensor matrix() {
    return matrix.copy();
  }
}

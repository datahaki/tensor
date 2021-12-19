// code by jph
package ch.alpine.tensor.red;

import java.util.Comparator;

import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArgMax.html">ArgMax</a> */
public class ArgMax extends ArgBase {
  /** @param tensor
   * @param comparator
   * @return index of maximum entry in tensor according to comparator,
   * or -1 if tensor is empty
   * @throws Exception if given tensor is a scalar */
  public static <T extends Tensor> int of(Tensor tensor, Comparator<T> comparator) {
    return new ArgMax().find(tensor, comparator);
  }

  /** Examples:
   * <pre>
   * ArgMax.of({3, 4, 2, 0, 3}) == 1
   * ArgMax.of({4, 3, 2, 4, 3}) == 0
   * </pre>
   * 
   * @param tensor
   * @return index of maximum entry in tensor, or -1 if tensor is empty
   * @throws Exception if given tensor is a scalar */
  public static <T extends Comparable<T>> int of(Tensor tensor) {
    return new ArgMax().find(tensor);
  }

  private ArgMax() {
    // ---
  }

  @Override // from IntPredicate
  public boolean test(int value) {
    return value < 0;
  }
}

// code by jph
package ch.alpine.tensor.red;

import java.util.Comparator;

import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArgMin.html">ArgMin</a> */
public class ArgMin extends ArgBase {
  /** @param tensor
   * @param comparator
   * @return index of minimum entry in tensor according to comparator,
   * or -1 if tensor is empty
   * @throws Exception if given tensor is a scalar */
  public static <T extends Tensor> int of(Tensor tensor, Comparator<T> comparator) {
    return new ArgMin().find(tensor, comparator);
  }

  /** Examples:
   * <pre>
   * ArgMin.of({3, 4, 1, 2, 3}) == 2
   * ArgMin.of({1, 4, 1, 2, 3}) == 0
   * </pre>
   * 
   * @param tensor
   * @return index of minimum entry in tensor, or -1 if tensor is empty
   * @throws Exception if given tensor is a scalar */
  public static <T extends Comparable<T>> int of(Tensor tensor) {
    return new ArgMin().find(tensor);
  }

  private ArgMin() {
    // ---
  }

  @Override // from IntPredicate
  public boolean test(int value) {
    return 0 < value;
  }
}

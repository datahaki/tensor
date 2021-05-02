// code by jph
package ch.alpine.tensor.alg;

import java.util.Iterator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/OrderedQ.html">OrderedQ</a> */
public enum OrderedQ {
  ;
  /** Hint: the entries of the given tensor have to be comparable.
   * For instance scalars of different unit are not comparable:
   * OrderedQ[{3[s], 1[s], 2[m]}] throws an Exception
   * 
   * @param tensor
   * @return whether entries in tensor are non-decreasing
   * @throws Exception if entries of given tensor cannot be compared */
  public static boolean of(Tensor tensor) {
    Iterator<Tensor> iterator = tensor.iterator();
    boolean status = true;
    if (iterator.hasNext()) {
      Tensor prev = iterator.next();
      while (iterator.hasNext())
        if (0 < TensorComparator.INSTANCE.compare(prev, prev = iterator.next()))
          status = false;
    }
    return status;
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if two entries in given tensor are decreasing */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }
}

// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** implementation is consistent with Mathematica::Sort
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sort.html">Sort</a>
 * 
 * @see Ordering 
 * @see TensorComparator */
public enum Sort {
  ;
  /** @param tensor
   * @return tensor with entries sorted according to canonic ordering
   * @throws Exception if input is a scalar */
  public static Tensor of(Tensor tensor) {
    return Tensor.of(tensor.stream() //
        .sorted(TensorComparator.INSTANCE) //
        .map(Tensor::copy));
  }

  /** @param vector
   * @param comparator
   * @return tensor with entries sorted according to given comparator */
  @SuppressWarnings("unchecked")
  public static <T extends Scalar> Tensor of(Tensor vector, Comparator<T> comparator) {
    return Tensor.of(vector.stream().map(scalar -> (T) scalar).sorted(comparator));
  }

  /** Hint: the comparator is not allowed to alter the content of the parameters,
   * otherwise the behavior is undefined.
   * 
   * @param tensor
   * @param comparator
   * @return tensor with entries sorted according to given comparator */
  public static Tensor ofTensor(Tensor tensor, Comparator<Tensor> comparator) {
    return Tensor.of(tensor.stream().sorted(comparator).map(Tensor::copy));
  }
}

// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/VectorQ.html">VectorQ</a> */
public enum VectorQ {
  ;
  /** @param tensor
   * @return true if all entries of given tensor are of type {@link Scalar} */
  public static boolean of(Tensor tensor) {
    return !ScalarQ.of(tensor) //
        && tensor.stream().allMatch(Scalar.class::isInstance);
  }

  /** @param tensor
   * @param length non-negative
   * @return true if tensor is a vector with given length */
  public static boolean ofLength(Tensor tensor, int length) {
    return tensor.length() == Integers.requirePositiveOrZero(length) //
        && tensor.stream().allMatch(Scalar.class::isInstance);
  }

  /** @param tensor
   * @throws Exception if given tensor is not a vector */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }

  /** @param tensor
   * @param length non-negative
   * @return given tensor
   * @throws Exception if given tensor is not a vector of length */
  public static Tensor requireLength(Tensor tensor, int length) {
    if (tensor.length() == length && //
        tensor.stream().allMatch(Scalar.class::isInstance))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }
}

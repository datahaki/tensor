// code by jph
package ch.alpine.tensor.alg;

import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Chop;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/VectorQ.html">VectorQ</a> */
public enum VectorQ {
  ;
  public static final ZeroDefectArrayQ INSTANCE = new ZeroDefectArrayQ(1, Chop.NONE) {
    @Override
    public Tensor defect(Tensor tensor) {
      return RealScalar.ZERO;
    }
  };

  public static ZeroDefectArrayQ ofLength(int length) {
    return new ZeroDefectArrayQ(1, Chop.NONE) {
      @Override
      protected boolean isArrayWith(List<Integer> list) {
        return list.get(0).equals(length);
      }

      @Override
      public Tensor defect(Tensor tensor) {
        return RealScalar.ZERO;
      }
    };
  }

  /** @param tensor
   * @return true if all entries of given tensor are of type {@link Scalar} */
  public static boolean of(Tensor tensor) {
    return !(tensor instanceof Scalar) //
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
    throw new Throw(tensor);
  }

  /** @param tensor
   * @param length non-negative
   * @return given tensor
   * @throws Exception if given tensor is not a vector of length */
  public static Tensor requireLength(Tensor tensor, int length) {
    if (tensor.length() == length && //
        tensor.stream().allMatch(Scalar.class::isInstance))
      return tensor;
    throw new Throw(tensor, length);
  }
}

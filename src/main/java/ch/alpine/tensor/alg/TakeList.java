// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Partition.html">Partition</a> */
public enum TakeList {
  ;
  /** Careful:
   * this implementation is stricter than Mathematicas!
   * 
   * @param tensor
   * @param lengths have to add up to tensor.length()
   * @return tensor of length lengths.length */
  public static Tensor of(Tensor tensor, int... lengths) {
    Tensor[] tensors = new Tensor[lengths.length];
    int beg = 0;
    for (int index = 0; index < lengths.length; ++index) {
      int end = beg + lengths[index];
      tensors[index] = tensor.extract(beg, end);
      beg = end;
    }
    Integers.requireEquals(beg, tensor.length());
    return Unprotect.byRef(tensors);
  }
}

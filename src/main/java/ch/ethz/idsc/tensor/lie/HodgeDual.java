// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.sca.Factorial;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HodgeDual.html">HodgeDual</a> */
public enum HodgeDual {
  ;
  /** @param tensor of array structure
   * @param d
   * @return */
  public static Tensor of(Tensor tensor, int d) {
    Dimensions dimensions = new Dimensions(tensor);
    if (dimensions.isArray()) {
      List<Integer> list = dimensions.list();
      int rank = list.size();
      if (!list.equals(Collections.nCopies(rank, d)))
        throw new IllegalArgumentException("" + list);
      // implementation is not efficient
      Tensor product = TensorProduct.of(tensor, LeviCivitaTensor.of(d));
      for (int index = 0; index < rank; ++index)
        product = TensorContract.of(product, 0, rank - index);
      return product.divide(Factorial.of(rank));
    }
    throw TensorRuntimeException.of(tensor);
  }
}

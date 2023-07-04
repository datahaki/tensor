// code by jph
package ch.alpine.tensor;

import java.util.List;

import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;

public enum UnprotectDepr {
  ;
  /** @param tensor
   * @return unique unit of quantities in given tensor
   * @throws Exception if quantities consist of mixed units */
  public static Unit getUnitUnique(Tensor tensor) {
    List<Unit> list = Flatten.scalars(tensor) //
        .map(QuantityUnit::of) //
        .distinct() //
        .limit(2).toList();
    if (list.size() == 1)
      return list.get(0);
    // list has at most 2 elements, so list.toString() is acceptable
    throw new IllegalArgumentException(list.toString());
  }
}

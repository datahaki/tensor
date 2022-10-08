// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableMap;
import java.util.NavigableSet;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;

public interface ResamplingMethod {
  /** @param navigableSet
   * @param function mapping key to value
   * @param x
   * @return result of resampling method at location x taking account the keys in the vicinity of x
   * and their corresponding values
   * @throws Exception if parameter x is is outside the interval
   * [navigableSet.first(), navigableSet.last()] */
  Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x);

  /** default implementation is
   * <pre>
   * return evaluate(navigableMap.navigableKeySet(), navigableMap::get, x);
   * </pre>
   * 
   * @param navigableMap
   * @param x
   * @return result of resampling method at location x taking account the keys in the vicinity of x
   * and their corresponding values
   * @throws Exception if parameter x is is outside the interval
   * [navigableMap.firstKey(), navigableMap.lastKey()] */
  Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x);

  /** inserts value in given navigableMap at key
   * 
   * the resampling methods may determine that the insertion of
   * the given (key, value)-pair is redundant and not alter the map
   * 
   * Careful: the method may not check if given value is structurally
   * consistent with other values in the map
   * 
   * @param navigableMap
   * @param key
   * @param value */
  void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value);

  /** @param navigableMap
   * @return given navigableMap but potentially with some entries removed */
  NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap);
}

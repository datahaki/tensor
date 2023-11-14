// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;

public interface ResamplingMethod {
  /** linear interpolation
   * default in Mathematica as "LinearInterpolation", and "{Interpolation, 1}" */
  ResamplingMethod LINEAR_INTERPOLATION = new Linear();
  ResamplingMethod LINEAR_INTERPOLATION_SPARSE = new LinearSparse();
  /** in Mathematica: HoldValueFromLeft */
  ResamplingMethod HOLD_VALUE_FROM_LEFT = new HoldLo();
  /** suitable for exact precision data sets */
  ResamplingMethod HOLD_VALUE_FROM_LEFT_SPARSE = new HoldLoSparse();
  /** in Mathematica: HoldValueFromRight */
  ResamplingMethod HOLD_VALUE_FROM_RIGHT = new HoldHi();
  /** suitable for exact precision data sets */
  ResamplingMethod HOLD_VALUE_FROM_RIGHT_SPARSE = new HoldHiSparse();
  /** in Mathematica: None */
  ResamplingMethod NONE = new None();

  /** @param navigableSet
   * @param function mapping key to value
   * @param x
   * @return result of resampling method at location x taking account the keys in the vicinity of x
   * and their corresponding values
   * @throws Exception if parameter x is outside the interval
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
   * @throws Exception if parameter x is outside the interval
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

  /** the method never removes the first or last element so as not to
   * alter the support
   * 
   * @param navigableMap
   * @return given navigableMap but potentially with some entries removed */
  NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap);

  Stream<Tensor> lines(NavigableMap<Scalar, Tensor> navigableMap);
}

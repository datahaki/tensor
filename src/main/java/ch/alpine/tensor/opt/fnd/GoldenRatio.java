// code by jph
package ch.alpine.tensor.opt.fnd;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;

public enum GoldenRatio {
  ;
  /** 1.618033988749895 */
  public static final Scalar VALUE = DoubleScalar.of((1 + Math.sqrt(5.0)) / 2.0);
}

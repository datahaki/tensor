// code by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ComplexInfinity.html">ComplexInfinity</a> */
public enum ComplexInfinity {
  ;
  public static final Scalar INSTANCE = ComplexScalar.of( //
      DoubleScalar.POSITIVE_INFINITY, //
      DoubleScalar.POSITIVE_INFINITY);
}

// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.SignInterface;

/** gives the real-valued cube root of a given scalar.
 * the input scalar has to be an instance of the {@link SignInterface}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CubeRoot.html">CubeRoot</a>
 * 
 * @see Surd */
public enum CubeRoot {
  ;
  public static final ScalarUnaryOperator FUNCTION = Surd.of(3);
}

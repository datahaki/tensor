// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface KurtosisInterface {
  /** @return kurtosis */
  Scalar kurtosis();
}

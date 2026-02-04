// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface TensorBinaryOperator extends BinaryOperator<Tensor>, Serializable {
  // ---
}

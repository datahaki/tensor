// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Floor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/NumberDecompose.html">NumberDecompose</a> */
public class NumberDecompose implements ScalarTensorFunction {
  /** @param vector
   * @return */
  public static ScalarTensorFunction of(Tensor vector) {
    return new NumberDecompose(vector);
  }

  // ---
  private final Tensor vector;

  private NumberDecompose(Tensor vector) {
    this.vector = ExactTensorQ.require(vector);
  }

  @Override
  public Tensor apply(Scalar scalar) {
    Tensor result = Tensors.reserve(vector.length());
    for (Tensor _s : vector) {
      Scalar factor = (Scalar) _s;
      Scalar floor = Floor.toMultipleOf(factor).apply(scalar);
      result.append(floor.divide(factor));
      scalar = scalar.subtract(floor);
    }
    return result;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("NumberDecompose", vector);
  }
}

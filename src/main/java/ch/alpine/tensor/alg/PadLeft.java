// code by jph
package ch.alpine.tensor.alg;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** Example:
 * <pre>
 * Mathematica::PadLeft[{1, 2, 3}, 6] == {0, 0, 0, 1, 2, 3}
 * Tensor::PadLeft.zeros(6).apply({1, 2, 3}) == {0, 0, 0, 1, 2, 3}
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PadLeft.html">PadLeft</a> */
public class PadLeft extends PadBase {
  /** @param element
   * @param dimensions non-empty
   * @return */
  public static TensorUnaryOperator with(Tensor element, List<Integer> dimensions) {
    dimensions.forEach(Integers::requirePositiveOrZero);
    return new PadLeft(Objects.requireNonNull(element), dimensions);
  }

  /** @param element
   * @param dimensions non-empty
   * @return */
  public static TensorUnaryOperator with(Tensor element, int... dimensions) {
    return new PadLeft(Objects.requireNonNull(element), Integers.asList(dimensions));
  }

  /** @param dimensions non-empty
   * @return */
  public static TensorUnaryOperator zeros(List<Integer> dimensions) {
    return with(RealScalar.ZERO, dimensions);
  }

  /** @param dimensions non-empty
   * @return */
  public static TensorUnaryOperator zeros(int... dimensions) {
    return zeros(Integers.asList(dimensions));
  }

  // ---
  private PadLeft(Tensor element, List<Integer> dimensions) {
    super(element, dimensions);
  }

  @Override // from PadBase
  protected Stream<Tensor> trim(Tensor tensor, int dim0) {
    return tensor.stream().skip(tensor.length() - dim0);
  }

  @Override // from PadBase
  protected Tensor join(Tensor tensor, Tensor pad) {
    return Join.of(pad, tensor);
  }

  @Override // from PadBase
  PadBase get(Tensor element, List<Integer> rest) {
    return new PadLeft(element, rest);
  }
}

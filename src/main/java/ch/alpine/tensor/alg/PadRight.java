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
 * Mathematica::PadRight[{1, 2, 3}, 6] == {1, 2, 3, 0, 0, 0}
 * Tensor::PadRight.zeros(6).apply({1, 2, 3}) == {1, 2, 3, 0, 0, 0}
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PadRight.html">PadRight</a> */
public class PadRight extends PadBase {
  /** @param element
   * @param dimensions non-empty
   * @return */
  public static TensorUnaryOperator with(Tensor element, List<Integer> dimensions) {
    dimensions.stream().forEach(Integers::requirePositiveOrZero);
    return new PadRight(Objects.requireNonNull(element), dimensions);
  }

  /** @param element
   * @param dimensions non-empty
   * @return */
  public static TensorUnaryOperator with(Tensor element, int... dimensions) {
    return with(element, Integers.asList(dimensions));
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
  private PadRight(Tensor element, List<Integer> dimensions) {
    super(element, dimensions);
  }

  @Override // from PadBase
  protected Stream<Tensor> trim(Tensor tensor, int dim0) {
    return tensor.stream().limit(dim0);
  }

  @Override // from PadBase
  protected Tensor join(Tensor tensor, Tensor pad) {
    return Join.of(tensor, pad);
  }

  @Override // from PadBase
  protected PadBase get(Tensor element, List<Integer> rest) {
    return new PadRight(element, rest);
  }
}

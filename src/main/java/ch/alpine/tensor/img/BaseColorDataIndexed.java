// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;

/* package */ abstract class BaseColorDataIndexed implements ColorDataIndexed {
  private final Tensor tensor;
  protected final Color[] colors;

  public BaseColorDataIndexed(Tensor tensor) {
    this.tensor = tensor;
    colors = tensor.stream() //
        .map(ColorFormat::toColor) //
        .toArray(Color[]::new);
  }

  @Override // from ScalarTensorFunction
  public final Tensor apply(Scalar scalar) {
    if (scalar instanceof MultiplexScalar)
      throw TensorRuntimeException.of(scalar);
    return FiniteScalarQ.of(scalar) //
        ? tensor.get(toInt(scalar))
        : Transparent.rgba();
  }

  @Override // from ColorDataIndexed
  public final int length() {
    return colors.length;
  }

  /** @param scalar
   * @return */
  protected abstract int toInt(Scalar scalar);

  /** @param alpha in the range [0, 1, ..., 255]
   * @return */
  protected final Tensor tableWithAlpha(int alpha) {
    return Tensor.of(tensor.stream().map(withAlpha(alpha)));
  }

  /** @param alpha in the range [0, 1, ..., 255]
   * @return operator that maps a vector of the form {r, g, b, any} to {r, g, b, alpha} */
  private static TensorUnaryOperator withAlpha(int alpha) {
    Scalar scalar = RealScalar.of(alpha);
    return rgba -> rgba.extract(0, 3).append(scalar);
  }
}

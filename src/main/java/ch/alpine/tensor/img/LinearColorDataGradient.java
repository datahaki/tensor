// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.MachineNumberQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.N;

/** ColorDataGradient maps a {@link Scalar} from the interval [0, 1] to a 4-vector
 * {r, g, b, a} with rgba entries using linear interpolation on a given table of rgba values.
 * 
 * <p>Each color component in the output tensor is an integer or double value in the
 * semi-open interval [0, 256). Because {@link ColorFormat} uses Number::intValue to
 * obtain the int color component, the value 256 is not allowed and results in an Exception.
 *
 * <p>In case NumberQ.of(scalar) == false then a transparent color is assigned.
 * The result is {0, 0, 0, 0}, which corresponds to a transparent color. */
public class LinearColorDataGradient implements ColorDataGradient {
  private static final Clip CLIP = Clips.interval(0, 256);

  /** colors are generated using {@link LinearInterpolation} of given tensor
   * 
   * @param tensor n x 4 where each row contains {r, g, b, a} with values ranging in [0, 256)
   * @return
   * @throws Exception if tensor is empty, or is not of the above form */
  public static ColorDataGradient of(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      throw TensorRuntimeException.of(tensor);
    tensor.stream().forEach(ColorFormat::toColor);
    return new LinearColorDataGradient(tensor.map(CLIP::requireInside));
  }

  /***************************************************/
  private final Tensor tensor;
  private final Interpolation interpolation;
  private final Scalar scale;

  /* package */ LinearColorDataGradient(Tensor tensor) {
    this.tensor = tensor;
    interpolation = LinearInterpolation.of(N.DOUBLE.of(tensor));
    scale = DoubleScalar.of(tensor.length() - 1);
  }

  @Override // from ColorDataGradient
  public Tensor apply(Scalar scalar) {
    Scalar value = scalar.multiply(scale); // throws Exception for GaussScalar
    return MachineNumberQ.of(value) //
        ? interpolation.at(value)
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public LinearColorDataGradient deriveWithOpacity(Scalar opacity) {
    return new LinearColorDataGradient(Tensor.of(tensor.stream().map(withOpacity(opacity))));
  }

  /** @param opacity in the interval [0, 1]
   * @return operator that maps a vector of the form rgba to rgb, alpha*factor
   * @throws Exception if given opacity is outside the valid range */
  private static TensorUnaryOperator withOpacity(Scalar opacity) {
    Clips.unit().requireInside(opacity);
    return rgba -> {
      Tensor copy = rgba.copy();
      copy.set(alpha -> alpha.multiply(opacity), 3);
      return copy;
    };
  }
}

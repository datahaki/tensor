// code by jph
package ch.alpine.tensor.img;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Rescale;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArrayPlot.html">ArrayPlot</a> */
public enum TensorArrayPlot {
  ;
  /** the given function is required to map values from the interval [0, 1] to vectors
   * of the form {R, G, B, A} with entries in the interval [0, 255].
   * 
   * <p>Hint: the function replaces scalar entries of given tensor with 4-vector that
   * encodes RGBA color values. If the input tensor does not have matrix structure,
   * the return tensor cannot be exported to an image.
   * 
   * @param tensor typically a matrix
   * @param function for instance {@link ColorDataGradients#CLASSIC}
   * @return */
  public static Tensor of(Tensor tensor, Function<Scalar, ? extends Tensor> function) {
    return Rescale.of(tensor).map(function);
  }
}

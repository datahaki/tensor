// code by jph
package ch.ethz.idsc.tensor;

/** Exception thrown when a problem is encountered related to the types
 * {@link Tensor}, and {@link Scalar}. */
public class TensorRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 8612741557842317758L;

  /** @param tensors
   * @return exception with message consisting of truncated string expressions of given tensors
   * @throws Exception if any of the listed tensors is null */
  public static TensorRuntimeException of(Tensor... tensors) {
    return new TensorRuntimeException(Tensors.message(tensors));
  }

  private TensorRuntimeException(String string) {
    super(string);
  }
}

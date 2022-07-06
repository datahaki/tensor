// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.io.MathematicaFormat;

/** Exception thrown when a problem is encountered related to the types
 * {@link Tensor}, and {@link Scalar}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Throw.html">Throw</a> */
public class Throw extends RuntimeException {
  /** @param objects
   * @return exception with message consisting of truncated string expressions of given tensors
   * @throws Exception if any of the listed tensors is null */
  @SafeVarargs
  public static Throw of(Object... objects) {
    return new Throw(MathematicaFormat.of("Throw", objects));
  }

  private Throw(String string) {
    super(string);
  }
}

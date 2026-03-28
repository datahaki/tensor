// code by jph
package ch.alpine.tensor;

import java.util.function.Supplier;

import ch.alpine.tensor.io.MathematicaFormat;

/** Exception thrown when a problem is encountered related to the types
 * {@link Tensor}, and {@link Scalar}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Throw.html">Throw</a> */
public class Throw extends RuntimeException {
  /** exception with message consisting of truncated string expressions of given objects
   * 
   * @param objects */
  public Throw(Object... objects) {
    this(MathematicaFormat.concise("Throw", objects));
  }

  private Throw(String string) {
    super(string);
  }

  /** @param enforce
   * @throws Exception unless enforce == true */
  public static void unless(boolean enforce) {
    if (!enforce)
      throw new Throw();
  }

  /** @param enforce
   * @param supplier
   * @throws Exception unless enforce == true with message provided by given supplier */
  public static void unless(boolean enforce, Supplier<String> supplier) {
    if (!enforce)
      throw new Throw(supplier.get());
  }
}

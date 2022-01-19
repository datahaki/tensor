// code by jph
package ch.alpine.tensor.usr;

import java.util.Objects;

/** for testing rejection of invalid, or erroneous input */
public enum AssertFail {
  ;
  /** @param runnable on which {@link Runnable#run()} in invoked which is expected
   * to throw an exception. The exception will be caught.
   * @throws AssertionError if runnable does not throw an {@link Exception} */
  public static void of(Runnable runnable) {
    Objects.requireNonNull(runnable);
    try {
      runnable.run(); // should throw an exception
      throw new AssertionError(); // used in junit 4.12
    } catch (Exception exception) {
      // caused by runnable.run() but not by assertion error
    }
  }
}

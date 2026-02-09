// code by jph
package ch.alpine.tensor.qty;

import java.util.Objects;

import ch.alpine.tensor.Scalar;

/** Timing with nanoseconds precision.
 * 
 * <p>The implementation behaves like a standard digital stopwatch:
 * <ul>
 * <li>the display shows 0:00:00 at the beginning.
 * <li>whenever started, the number on the display continuously increases.
 * <li>whenever stopped, the number on the display is frozen.
 * <li>the number on the display can be read any time.
 * </ul>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Timing.html">Timing</a> */
public class Timing {
  private static final Unit NS = Unit.of("ns");
  private static final Unit S = Unit.of("s");

  /** @return new instance of {@code Timing} that is started upon creation */
  public static Timing started() {
    Timing timing = new Timing();
    timing.start();
    return timing;
  }

  /** @return new instance of {@code Timing} that is in the stopped state
   * with display reading 0:00:00 */
  public static Timing stopped() {
    return new Timing();
  }

  // ---
  /** last frozen display, initialized at 0:00:00 */
  private long frozen;
  /** last internal {@link System#nanoTime()} time when timing was started */
  private Long tic;

  /** constructor creates an instance that is not started, i.e. the display
   * is frozen at 0:00:00. */
  private Timing() {
  }

  /** start timing
   * 
   * @throws Exception if timing is already started */
  public void start() {
    if (!isStopped())
      throw new IllegalStateException();
    tic = System.nanoTime();
  }

  /** stop timing.
   * 
   * <p>function does not return anything.
   * use {@link #seconds()} or {@link #nanoSeconds()} to obtain absolute time.
   * 
   * @throws Exception if timing is not started */
  public void stop() {
    frozen += current();
    tic = null;
  }

  private long _nanos() {
    return frozen + (isStopped() ? 0 : current());
  }

  /** @return what is on the display of the timing in nanoseconds:
   * total of all start-until-stop intervals including start-until-now
   * if instance is started in exact precision */
  public Scalar nanoSeconds() {
    return Quantity.of(_nanos(), NS);
  }

  /** @return what is on the display of the timing in seconds:
   * total of all start-until-stop intervals including start-until-now
   * if instance is started in machine precision */
  public Scalar seconds() {
    return Quantity.of(_nanos() * 1e-9, S);
  }

  /** @return true if timing is not started */
  // function is private because state of timing can be tracked in the application layer
  private boolean isStopped() {
    return Objects.isNull(tic);
  }

  /** @return internal time difference start-until-now
   * @throws Exception if timing is not started */
  private long current() {
    return System.nanoTime() - tic;
  }
}

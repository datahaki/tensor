// code by jph
package ch.alpine.tensor.itp;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

/** Given a continuous function over a 1-dimensional domain, {@link FindRoot}
 * finds a value x inside a provided search interval [x0, x1] so that
 * approximately function(x) == 0.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FindRoot.html">FindRoot</a> */
public class FindRoot implements Serializable {
  /** the max iterations was chosen with the following consideration:
   * 
   * When starting with the unit interval as search interval, 40 iterations
   * of halving the interval results a width |x0-x1| below 1E-12, i.e.
   * {@link Tolerance#CHOP}.
   * 
   * Every second iteration step is guaranteed to half the search interval,
   * that means after 80 iterations the search interval has at least shrunk
   * by the factor 1E-12.
   * 
   * The abort criteria concerns the function values. If the function
   * is very steep, the search interval may need to be reduced to
   * 1E-16 for instance, so we add a few iterations more. */
  private static final int MAX_ITERATIONS = 128;
  private static final Scalar HALF = RealScalar.of(0.5);

  /** @param function continuous
   * @return */
  public static FindRoot of(ScalarUnaryOperator function) {
    return of(function, Tolerance.CHOP);
  }

  /** @param function continuous
   * @param chop accuracy that determines function(x) is sufficiently close to 0
   * @return */
  public static FindRoot of(ScalarUnaryOperator function, Chop chop) {
    return new FindRoot(function, chop);
  }

  // ---
  private final ScalarUnaryOperator function;
  private final Chop chop;

  /** @param function
   * @param chop */
  private FindRoot(ScalarUnaryOperator function, Chop chop) {
    this.function = function;
    this.chop = chop;
  }

  /** @param clip search interval
   * @return x inside clip so that function(x) == 0 with given chop accuracy
   * @throws Exception if function(clip.min()) and function(clip.max()) have the same sign unequal to zero */
  public Scalar inside(Clip clip) {
    return inside(clip, function.apply(clip.min()), function.apply(clip.max()));
  }

  /** @param clip search interval
   * @param y0 == function(clip.min())
   * @param y1 == function(clip.max())
   * @return x inside clip so that function(x) == 0 with given chop accuracy
   * @throws Exception if function(clip.min()) and function(clip.max()) have the same sign unequal to zero */
  public Scalar inside(Clip clip, Scalar y0, Scalar y1) {
    if (chop.isZero(y0))
      return clip.min();
    if (chop.isZero(y1))
      return clip.max();
    // ---
    final Scalar s0 = Sign.FUNCTION.apply(y0); // s0 is never 0
    final Scalar s1 = Sign.FUNCTION.apply(y1); // s1 is never 0
    if (s0.equals(s1))
      throw new Throw(clip, y0, y1);
    // ---
    for (int index = 0; index < MAX_ITERATIONS; ++index) {
      Scalar xn = index % 2 == 0 //
          ? (Scalar) LinearBinaryAverage.INSTANCE.split(clip.min(), clip.max(), HALF)
          : linear(clip, y0, y1);
      // ---
      Scalar yn = function.apply(xn);
      // ---
      if (chop.isZero(yn)) {
        // System.out.println(index);
        return xn;
      }
      Scalar sn = Sign.FUNCTION.apply(yn); // sn is never 0
      if (s0.equals(sn)) { // s0 == sn
        clip = Clips.interval(xn, clip.max());
        y0 = yn;
      } else { // s1 == sn
        clip = Clips.interval(clip.min(), xn);
        y1 = yn;
      }
    }
    throw new Throw(clip, y0, y1);
  }

  /** Function is equivalent to
   * <pre>
   * Polynomial.fit(Tensors.of(clip.min(), clip.max()), Tensors.of(y0, y1), 1).roots().Get(0);
   * </pre>
   * 
   * Functionality is implemented explicitly for speed.
   * 
   * @param clip
   * @param y0
   * @param y1
   * @return (x0 y1 - x1 y0) / (y1 - y0) */
  @PackageTestAccess
  static Scalar linear(Clip clip, Scalar y0, Scalar y1) {
    return clip.min().multiply(y1).subtract(clip.max().multiply(y0)).divide(y1.subtract(y0));
  }
}

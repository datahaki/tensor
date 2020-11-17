// code by jph
package ch.ethz.idsc.tensor.lie.r2;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.ext.Integers;

/** implementation is only consistent with Mathematica up to rotation around coordinate (0, 0)
 * 
 * <p>For instance, the points
 * Mathematica::CirclePoints[3] == {{Sqrt[3]/2, -(1/2)}, {0, 1}, {-(Sqrt[3]/2), -(1/2)}}
 * are mapped onto
 * Tensor::CirclePoints[3] == {{1.000, 0.000}, {-0.500, 0.866}, {-0.500, -0.866}}
 * using a rotation around (0, 0) by Pi/6.
 * 
 * <p>aspects consistent with Mathematica
 * CirclePoints[0] == {}
 * CirclePoints[-1] throws an Exception
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CirclePoints.html">CirclePoints</a> */
public enum CirclePoints {
  ;
  private static final int MAX_SIZE = 16;
  private static final Function<Integer, Tensor> CACHE = Cache.of(CirclePoints::function, MAX_SIZE);

  /** the first coordinate is always {1, 0}.
   * the orientation of the points is counter-clockwise.
   * 
   * if n == 0 the return value is the empty tensor {}
   * 
   * @param n non-negative integer
   * @return n x 2 matrix with evenly spaced points on the unit-circle
   * @throws Exception if n is negative */
  public static Tensor of(int n) {
    return CACHE.apply(Integers.requirePositiveOrZero(n)).copy();
  }

  // helper function
  private static Tensor function(int n) {
    return Range.of(0, n).divide(RealScalar.of(n)).map(AngleVector::turns);
  }
}

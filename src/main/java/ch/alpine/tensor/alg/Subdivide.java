// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;

/** Subdivide is consistent with Mathematica.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Subdivide.html">Subdivide</a>
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support
 * 
 * @see Range */
public enum Subdivide {
  ;
  /** Subdivides interpolates between given start and end with n-1 intermediate points.
   * The return value is a tensor with first element identical to given start and
   * the last element identical to given end.
   * 
   * <p>Examples:
   * <pre>
   * Subdivide.of(10, 15, 5) == {10, 11, 12, 13, 14, 15}
   * Subdivide.of(10, 15, 4) == {10, 45/4, 25/2, 55/4, 15}
   * </pre>
   * 
   * Subdivide operates on decreasing values:
   * <pre>
   * Subdivide.of(RealScalar.of(-1), RealScalar.of(-4), 3) == {-1, -2, -3, -4}
   * </pre>
   * 
   * Subdivide operates on vectors:
   * <pre>
   * Subdivide.of(Tensors.vector(10, 5), Tensors.vector(5, 15), 4)
   * </pre>
   * 
   * Subdivide operates on {@link Quantity}:
   * <pre>
   * Subdivide.of(Quantity.of(-20, "deg"), Quantity.of(20, "deg"), 4)
   * == {-20[deg], -10[deg], 0[deg], 10[deg], 20[deg]}
   * </pre>
   * 
   * @param startInclusive
   * @param endInclusive
   * @param n strictly positive
   * @return tensor with n+1 entries obtained by subdividing the range
   * startInclusive to endInclusive into n equal parts.
   * @throws Exception if n is negative or zero */
  public static Tensor of(Tensor startInclusive, Tensor endInclusive, int n) {
    Integers.requirePositive(n);
    Tensor delta = endInclusive.subtract(startInclusive);
    return Tensor.of(Stream.concat( //
        IntStream.range(0, n) //
            .mapToObj(count -> startInclusive.add(delta.multiply(Rational.of(count, n)))), //
        Stream.of(endInclusive.copy())));
  }

  /** see description above
   * 
   * @param startInclusive
   * @param endInclusive
   * @param n strictly positive
   * @return tensor with n+1 entries obtained by subdividing the range */
  public static Tensor of(Number startInclusive, Number endInclusive, int n) {
    return of(RealScalar.of(startInclusive), RealScalar.of(endInclusive), n);
  }

  /** Example:
   * <pre>
   * Clip clip = Clip.function(Quantity.of(+20, "m"), Quantity.of(+40, "m"));
   * Subdivide.of(clip, 4) == Tensors.fromString("{20[m], 25[m], 30[m], 35[m], 40[m]}"));
   * </pre>
   * 
   * @param clip
   * @param n strictly positive
   * @return Subdivide.increasing(clip.min(), clip.max(), n) */
  public static Tensor increasing(Clip clip, int n) {
    return of(clip.min(), clip.max(), n);
  }

  /** Example:
   * <pre>
   * Clip clip = Clip.function(Quantity.of(+20, "m"), Quantity.of(+40, "m"));
   * Subdivide.decreasing(clip, 4) == Tensors.fromString("{40[m], 35[m], 30[m], 25[m], 20[m]}"));
   * </pre>
   * 
   * @param clip
   * @param n strictly positive
   * @return Subdivide.of(clip.max(), clip.min(), n) */
  public static Tensor decreasing(Clip clip, int n) {
    return of(clip.max(), clip.min(), n);
  }

  /** @param clip
   * @param n strictly positive
   * @return tensor of length n (!) */
  public static Tensor intermediate_increasing(Clip clip, int n) {
    Scalar delta = clip.width().divide(RealScalar.of(2 * n));
    Scalar startInclusive = clip.min().add(delta);
    if (n == 1)
      return Tensors.of(startInclusive);
    return of(startInclusive, clip.max().subtract(delta), n - 1);
  }

  /** @param clip
   * @param n strictly positive
   * @return tensor of length n (!) */
  public static Tensor intermediate_decreasing(Clip clip, int n) {
    Scalar delta = clip.width().divide(RealScalar.of(2 * n));
    Scalar startInclusive = clip.max().subtract(delta);
    if (n == 1)
      return Tensors.of(startInclusive);
    return of(startInclusive, clip.min().add(delta), n - 1);
  }
}

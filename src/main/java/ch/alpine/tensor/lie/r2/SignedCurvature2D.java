// code by jph
// adapted from document by Tobias Ewald
// sign convention by gjoel
package ch.alpine.tensor.lie.r2;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.nrm.Vector2Norm;

/** The implementation supports the use of Quantity.
 * The signed curvature is a Quantity with negated unit,
 * because curvature is the reciprocal of the radius.
 * 
 * Example: if the vectors are specified in coordinates with Unit "m"
 * then the function outputs values with unit "m^-1".
 * 
 * Sign convention:
 * For points sampled from a circle in counter clockwise direction
 * the result is positive, i.e. the inverse of the radius of the circle.
 * For instance: for the points {1, 0}, {0, 1}, {-1, 0} from the
 * unit circle the result is +1. */
public enum SignedCurvature2D {
  ;
  /** @param a vector of length 2
   * @param b vector of length 2
   * @param c vector of length 2
   * @return inverse of radius of circle that interpolates the given points a, b, c,
   * or Optional.empty() if any two of the three points are identical */
  public static Optional<Scalar> of(Tensor a, Tensor b, Tensor c) {
    Tensor d_ab = b.subtract(a);
    Scalar v = Det2D.of(d_ab, c.subtract(b));
    Scalar w = (Scalar) d_ab.dot(c.subtract(a));
    Scalar n = Vector2Norm.between(c, b);
    Scalar den = Hypot.of(v, w).multiply(n);
    return Scalars.isZero(den) //
        ? Optional.empty()
        : Optional.of(v.add(v).divide(den)); // 2 * v / den == (v + v) / den
  }
}

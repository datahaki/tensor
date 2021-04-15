// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Hypot computes
 * <code>sqrt(<i>a</i><sup>2</sup>&nbsp;+<i>b</i><sup>2</sup>)</code>
 * for a and b as {@link RealScalar}s
 * without intermediate overflow or underflow.
 * 
 * <p>Hypot also operates on vectors.
 * 
 * <p>Hypot is inspired by {@link Math#hypot(double, double)} */
public enum Hypot {
  ;
  /** @param a
   * @param b
   * @return Sqrt[ |a|^2 + |b|^2 ] */
  public static Scalar of(Scalar a, Scalar b) {
    Scalar ax = Abs.FUNCTION.apply(a);
    Scalar ay = Abs.FUNCTION.apply(b);
    if (Scalars.isZero(ax) || Scalars.isZero(ay))
      return ax.add(ay);
    final Scalar max;
    Scalar r1 = Scalars.lessThan(ax, ay) //
        ? ax.divide(max = ay)
        : ay.divide(max = ax);
    // valid at this point: 0 < max
    Scalar r2 = r1.multiply(r1);
    return Sqrt.FUNCTION.apply(r2.one().add(r2)).multiply(max);
  }

  /** @param a without unit
   * @return Sqrt[ |a|^2 + 1 ] */
  public static Scalar withOne(Scalar a) {
    Scalar ax = Abs.FUNCTION.apply(a);
    Scalar one = ax.one();
    if (Scalars.lessThan(ax, one))
      return Sqrt.FUNCTION.apply(ax.multiply(ax).add(one));
    Scalar r1 = ax.reciprocal(); // in the unit interval [0, 1]
    return Sqrt.FUNCTION.apply(r1.multiply(r1).add(one)).multiply(ax);
  }

  /** function computes the 2-Norm of a vector
   * without intermediate overflow or underflow
   * 
   * <p>the empty vector Hypot[{}] results in an error, since
   * Mathematica::Norm[{}] == Norm[{}] is undefined also.
   * 
   * <p>The disadvantage of the implementation is that
   * a numerical output is returned even in cases where
   * a rational number is the exact result.
   * 
   * @param vector
   * @return 2-norm of vector
   * @throws Exception if vector is empty, or vector contains NaN */
  public static Scalar ofVector(Tensor vector) {
    // same issue as in Pivots
    Tensor abs = vector.map(Abs.FUNCTION);
    Scalar max = abs.stream() //
        .map(Scalar.class::cast) //
        // .filter(Scalars::nonZero) //
        .reduce(Max::of).get();
    if (Scalars.isZero(max))
      return max;
    abs = abs.divide(max);
    return max.multiply(Sqrt.FUNCTION.apply((Scalar) abs.dot(abs)));
  }
}

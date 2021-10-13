// code by jph
package ch.alpine.tensor.itp;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.Integers;

/** The implementation of BSplineFunction in the tensor library is different from Mathematica.
 * 
 * tensor::BSplineFunction is parameterized over the interval [0, control.length() - 1]
 * 
 * tensor::BSplineFunction can be instantiated for all degrees regardless of the length of
 * the control points.
 * 
 * Mathematica::BSplineFunction throws an exception if number of control points is
 * insufficient for the specified degree.
 * 
 * tensor::BSplineFunction uses uniform knot spacing except for string alignment at the
 * terminal points.
 * 
 * <p>Quote from Wikipedia:
 * The term "B-spline" was coined by Isaac Jacob Schoenberg and is short for basis spline.
 * A spline is a piecewise polynomial function of a given degree in a variable x. The values
 * of x where the pieces of polynomial meet are known as knots, denoted ..., t0, t1, t2, ...
 * and sorted into non-decreasing order. */
public abstract class BSplineFunction implements ScalarTensorFunction {
  private static final int CACHE_SIZE = 16;
  // ---
  private final Cache<Integer, DeBoor> cache = Cache.of(new Inner(), CACHE_SIZE);
  private final BinaryAverage binaryAverage;
  protected final int degree;
  private final Tensor sequence;
  /** half == degree / 2 */
  private final int half;
  /** shift is 0 for odd degree and 1/2 for even degree */
  protected final Scalar shift;

  protected BSplineFunction(BinaryAverage binaryAverage, int degree, Tensor sequence) {
    this.binaryAverage = Objects.requireNonNull(binaryAverage);
    this.degree = Integers.requirePositiveOrZero(degree);
    this.sequence = Objects.requireNonNull(sequence);
    half = degree / 2;
    shift = Integers.isEven(degree) //
        ? RationalScalar.HALF
        : RealScalar.ZERO;
  }

  /** @param k
   * @return */
  public final DeBoor deBoor(int k) {
    return cache.apply(k);
  }

  private class Inner implements Function<Integer, DeBoor>, Serializable {
    @Override
    public DeBoor apply(Integer k) {
      return new DeBoor(binaryAverage, degree, knots(k), //
          Tensor.of(IntStream.range(k - half, k + degree + 1 - half) // control
              .map(BSplineFunction.this::bound) //
              .mapToObj(sequence::get)));
    }
  }

  /** @param k
   * @return knot vector corresponding to interval k */
  protected abstract Tensor knots(int k);

  /** @param index
   * @return index mapped to position in control point sequence */
  protected abstract int bound(int index);
}

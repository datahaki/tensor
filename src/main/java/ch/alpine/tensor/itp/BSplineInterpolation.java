// code by jph
package ch.alpine.tensor.itp;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.mat.re.LinearSolve;

/** BSplineInterpolation defines a parametric curve that interpolates
 * the given control points at integer values.
 * 
 * <p>The input to {@link #get(Tensor)} is required to be non-empty. */
public class BSplineInterpolation extends AbstractInterpolation implements Serializable {
  /** @param degree of b-spline basis functions: 1 for linear, 2 for quadratic, etc.
   * @param control points with at least one element
   * @return */
  public static Interpolation of(int degree, Tensor control) {
    return new BSplineInterpolation(degree, control);
  }

  /** @param degree of b-spline basis functions: 1 for linear, 2 for quadratic, etc.
   * @param n number of control points
   * @return */
  public static Tensor matrix(int degree, int n) {
    Tensor domain = Range.of(0, n);
    return Transpose.of(Tensor.of(IntStream.range(0, n) //
        .mapToObj(index -> domain.map(BSplineFunctionString.of(degree, UnitVector.of(n, index))))));
  }

  /** @param degree
   * @param control
   * @return control points that define a limit that interpolates the points in the given tensor */
  public static Tensor solve(int degree, Tensor control) {
    return LinearSolve.of(matrix(degree, control.length()), control);
  }

  // ---
  private final int degree;
  private final ScalarTensorFunction scalarTensorFunction;

  private BSplineInterpolation(int degree, Tensor control) {
    this.degree = degree;
    scalarTensorFunction = BSplineFunctionString.of(degree, solve(degree, control));
  }

  @Override // from Interpolation
  public Tensor get(Tensor index) {
    Tensor interp = at(index.Get(0));
    // TODO TENSOR IMPL can be improved by truncating data to a neighborhood
    return index.length() == 1 //
        ? interp
        : of(degree, interp).get(Tensor.of(index.stream().skip(1)));
  }

  @Override // from Interpolation
  public Tensor at(Scalar index) {
    return scalarTensorFunction.apply(index);
  }
}
